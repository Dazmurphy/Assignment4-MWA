package assign.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import assign.domain.Project;

public class ProjectServiceImpl implements ProjectService {

	String dbURL = "";
	String dbUsername = "";
	String dbPassword = "";
	DataSource ds;

	// DB connection information would typically be read from a config file.
	public ProjectServiceImpl(String dbUrl, String username, String password) {
		this.dbURL = dbUrl;
		this.dbUsername = username;
		this.dbPassword = password;
		
		ds = setupDataSource();
	}
	
	public DataSource setupDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUsername(this.dbUsername);
        ds.setPassword(this.dbPassword);
        ds.setUrl(this.dbURL);
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        return ds;
    }
	
	public Project addProject(Project p) throws Exception {
		Connection conn = ds.getConnection();
		
		String insert = "INSERT INTO projects(name, description) VALUES(?, ?)";
		PreparedStatement stmt = conn.prepareStatement(insert,
                Statement.RETURN_GENERATED_KEYS);
		
		stmt.setString(1, p.getName());
		stmt.setString(2, p.getDescription());
		
		int affectedRows = stmt.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating project failed, no rows affected.");
        }
        
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
        	p.setProjectId(generatedKeys.getInt(1));
        }
        else {
            throw new SQLException("Creating project failed, no ID obtained.");
        }
        
        // Close the connection
        conn.close();
        
		return p;
	}

	public Project getProject(int projectId) throws Exception {
		String query = "select * from projects where projectId=" + projectId;
		Connection conn = ds.getConnection();
		PreparedStatement s = conn.prepareStatement(query);
		ResultSet r = s.executeQuery();
		
		if (!r.next()) {
			return null;
		}
		
		Project p = new Project();
		p.setDescription(r.getString("description"));
		p.setName(r.getString("name"));
		p.setProjectId(r.getInt("projectId"));
		return p;
	}


	public Project updateProject(Project p) throws Exception {
		Connection conn = ds.getConnection();
		String update = "UPDATE projects SET name = ?, description = ? WHERE projectId = ?";
		
		PreparedStatement s = conn.prepareStatement(update, Statement.RETURN_GENERATED_KEYS);
		
		s.setString(1, p.getName());
		s.setString(2, p.getDescription());
		s.setInt(3, p.getProjectId());
		
		int affectedRows = s.executeUpdate();
		

        if (affectedRows == 0) {
            throw new SQLException("Updating project failed, no rows affected.");
        }
		
        conn.close();
		
		return p;
	}


	public Project deleteProject(int projectId) throws Exception {
		
		Connection conn = ds.getConnection();
		String delete = "DELETE from projects WHERE projectId = ?";
		
		PreparedStatement s = conn.prepareStatement(delete, Statement.RETURN_GENERATED_KEYS);
		
		s.setInt(1, projectId);
		
		int affectedRows = s.executeUpdate();
		

        if (affectedRows == 0) {
            throw new SQLException("Deleting project failed, no rows affected.");
        }
		
        conn.close();
		
		return null;
	}

	@Override
	public boolean checkProjectId(int projectId) throws Exception {
		Connection conn = ds.getConnection();
		String checkId = "SELECT * from projects WHERE projectId = ?";
		
		PreparedStatement s = conn.prepareStatement(checkId, Statement.RETURN_GENERATED_KEYS);
		
		s.setInt(1, projectId);
		
		ResultSet affectedRows = s.executeQuery();
		
		affectedRows.last();
		int count = affectedRows.getRow();
		
        if (count == 0) {
            return false;
        }
		
        conn.close();
		
		return true;
	}
}
