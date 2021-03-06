package com.blocklang.develop.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blocklang.core.constant.CmPropKey;
import com.blocklang.core.exception.NoAuthorizationException;
import com.blocklang.core.exception.ResourceNotFoundException;
import com.blocklang.core.service.PropertyService;
import com.blocklang.develop.designer.data.Dependence;
import com.blocklang.develop.designer.data.PageModel;
import com.blocklang.develop.designer.data.RepoWidgetList;
import com.blocklang.develop.model.Project;
import com.blocklang.develop.model.ProjectResource;
import com.blocklang.develop.service.ProjectDependenceService;
import com.blocklang.develop.service.ProjectResourceService;
import com.blocklang.marketplace.data.LocalRepoPath;
import com.blocklang.marketplace.model.ComponentRepo;

/**
 * 页面设计器专用的控制器放在此处集中维护。
 * 
 * @author jinzw
 *
 */
@RestController
public class PageDesignerController extends AbstractProjectController {
	
	@Autowired
	private ProjectDependenceService projectDependenceService;
	@Autowired
	private ProjectResourceService projectResourceService;
	@Autowired
	private PropertyService propertyService;
	
	/**
	 * 与 {@link ProjectDependenceController#getDependence(Principal, String, String)}} 功能类似，
	 * 但是一个是在项目依赖的维护页面中使用的，一个是在页面设计器中使用的。
	 * 
	 * @param principal
	 * @param owner
	 * @param projectName
	 * @return
	 */
	@GetMapping("/designer/projects/{projectId}/dependences")
	public ResponseEntity<List<Dependence>> listProjectDependences(
			Principal principal,
			@PathVariable Integer projectId,
			@RequestParam String category) {
		
		if(!category.equalsIgnoreCase("dev")) {
			throw new UnsupportedOperationException("当前仅支持获取 dev 依赖。");
		}
		Project project = projectService.findById(projectId).orElseThrow(ResourceNotFoundException::new);
		projectPermissionService.canRead(principal, project).orElseThrow(NoAuthorizationException::new);
		
		List<Dependence> result = projectDependenceService.findProjectDependences(project.getId(), true).stream()
				.filter(item -> item.getComponentRepo().getIsIdeExtension()).map(item -> {
					Dependence dependence = new Dependence();

					ComponentRepo componentRepo = item.getComponentRepo();
					dependence.setId(componentRepo.getId());
					dependence.setGitRepoWebsite(componentRepo.getGitRepoWebsite());
					dependence.setGitRepoOwner(componentRepo.getGitRepoOwner());
					dependence.setGitRepoName(componentRepo.getGitRepoName());
					dependence.setName(componentRepo.getName());
					dependence.setCategory(componentRepo.getCategory().getValue());
					dependence.setStd(componentRepo.isStd());

					dependence.setVersion(item.getComponentRepoVersion().getVersion());

					dependence.setApiRepoId(item.getApiRepo().getId());
					return dependence;
				}).collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}
	
	/**
	 * 获取项目依赖的 API 组件库中类型为 Widget 的组件库中的所有部件。
	 * 并按照组件库和部件种类分组。
	 * 
	 * @return
	 */
	@GetMapping("/designer/projects/{projectId}/dependences/widgets")
	public ResponseEntity<List<RepoWidgetList>> getProjectDependenceWidgets(
			Principal principal,
			@PathVariable Integer projectId) {
		Project project = projectService.findById(projectId).orElseThrow(ResourceNotFoundException::new);
		projectPermissionService.canRead(principal, project).orElseThrow(NoAuthorizationException::new);
		
		List<RepoWidgetList> result = projectDependenceService.findAllWidgets(project.getId());
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/designer/pages/{pageId}/model")
	public ResponseEntity<PageModel> getPageModel(
			Principal principal, 
			@PathVariable Integer pageId) {
		ProjectResource page = projectResourceService.findById(pageId).orElseThrow(ResourceNotFoundException::new);
		if(!page.isPage()) {
			throw new ResourceNotFoundException();
		}
		Project project = projectService.findById(page.getProjectId()).orElseThrow(ResourceNotFoundException::new);
		projectPermissionService.canRead(principal, project).orElseThrow(NoAuthorizationException::new);
		
		PageModel result = projectResourceService.getPageModel(project.getId(), page.getId());
		return ResponseEntity.ok(result);
	}
	
	@PutMapping("/designer/pages/{pageId}/model")
	public ResponseEntity<Map<String, Object>> updatePageModel(
			Principal principal, 
			@PathVariable Integer pageId, 
			@RequestBody PageModel model ) {
		// ensure user login
		if(principal == null) {
			throw new NoAuthorizationException();
		}

		ProjectResource page = projectResourceService.findById(pageId).orElseThrow(ResourceNotFoundException::new);
		if(!page.isPage()) {
			throw new ResourceNotFoundException();
		}
		Project project = projectService.findById(page.getProjectId()).orElseThrow(ResourceNotFoundException::new);
		projectPermissionService.canWrite(principal, project).orElseThrow(NoAuthorizationException::new);
		
		projectResourceService.updatePageModel(project, page, model);
		
		return ResponseEntity.noContent().build();
	}
	
	private static final String[] VALID_ASSET_NAMES = {"main.bundle.js", "main.bundle.js.map", "main.bundle.css", "main.bundle.css.map", "icons.svg"};
	@GetMapping("/designer/assets/{gitRepoWebsite}/{gitRepoOwner}/{gitRepoName}/{version}/{fileName}")
	public ResponseEntity<InputStreamSource> getAsset(
			@PathVariable String gitRepoWebsite,
			@PathVariable String gitRepoOwner,
			@PathVariable String gitRepoName,
			@PathVariable String version,
			@PathVariable String fileName) {
		
		Arrays.stream(VALID_ASSET_NAMES).filter(item -> item.equals(fileName)).findAny().orElseThrow(ResourceNotFoundException::new);
		
		String dataRootPath = propertyService.findStringValue(CmPropKey.BLOCKLANG_ROOT_PATH, "");
		LocalRepoPath localRepoPath = new LocalRepoPath(dataRootPath, gitRepoWebsite, gitRepoOwner, gitRepoName);
		Path filePath = localRepoPath.getRepoPackageDirectory().resolve(version).resolve(fileName);
		
		if(Files.notExists(filePath)) {
			throw new ResourceNotFoundException();
		}
		
		try {
			InputStream io = ResourceUtils.getURL(ResourceUtils.FILE_URL_PREFIX + filePath.toString()).openStream();
			InputStreamResource resource = new InputStreamResource(io);
			
			MediaType contentType = MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM);
			return ResponseEntity.ok().contentType(contentType).body(resource);
		} catch (IOException e) {
			throw new ResourceNotFoundException();
		}
	}
}
