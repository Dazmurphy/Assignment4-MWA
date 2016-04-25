package assign.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import assign.domain.Project;
import assign.services.ProjectService;
import assign.services.ProjectServiceImpl;

@Path("/myeavesdrop")
public class ProjectResource {
	
	ProjectService projectService;
	String password;
	String username;
	String dburl;	
	
	public ProjectResource(@Context ServletContext servletContext) {
		dburl = servletContext.getInitParameter("DBURL");
		username = servletContext.getInitParameter("DBUSERNAME");
		password = servletContext.getInitParameter("DBPASSWORD");
		this.projectService = new ProjectServiceImpl(dburl, username, password);		
	}
	
	@POST
	@Path("/projects")
	@Consumes(MediaType.APPLICATION_XML)
	public Response postProject(Project p) throws Exception {
		
		if(p.getDescription().equals("") || p.getName().equals("")){
			return Response.status(400).build();
		}
		
		projectService.addProject(p);
		
		URI uri = new URI("http://localhost:8080/assignment4/myeavesdrop/projects/" + p.getProjectId());
		return Response.created(uri).build();
	}
	
	@PUT
	@Path("/projects/{projectId}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response putProject(Project p, @PathParam("projectId") int projectId) throws Exception{
		
		p.setProjectId(projectId);
		
		if(p.getDescription().equals("") || p.getName().equals("") || projectService.checkProjectId(projectId) == false){
			return Response.status(400).build();
		}
		
		projectService.updateProject(p);
		
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/projects/{projectId}")
	public Response deleteProject(@PathParam("projectId") int projectId) throws Exception{
		
		if(projectService.checkProjectId(projectId) == false){
			return Response.status(404).build();
		}
		
		projectService.deleteProject(projectId);
		
		return Response.ok().build();
	}
	
	@GET
	@Path("/projects/{projectId}")
	@Produces("application/xml")
	public Response getProject(@PathParam("projectId") int projectId) throws Exception {
		
		if(projectService.checkProjectId(projectId) == false){
			return Response.status(404).build();
		}
		
		Project p = projectService.getProject(projectId);
		
		return Response.ok(p, MediaType.APPLICATION_XML).build();
	     
	}
}