package assign.services;

import assign.domain.Project;

public interface ProjectService {

	public Project addProject(Project p) throws Exception;

	public Project getProject(int projectId) throws Exception;

	public Project updateProject(Project p) throws Exception;

	public Project deleteProject(int projectId) throws Exception;

	public boolean checkProjectId(int projectId) throws Exception;

}
