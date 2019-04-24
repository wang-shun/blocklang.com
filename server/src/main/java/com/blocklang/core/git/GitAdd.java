package com.blocklang.core.git;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blocklang.core.git.exception.GitAddFailedException;
import com.blocklang.core.git.exception.GitRepoNotFoundException;

public class GitAdd {

	private static final Logger logger = LoggerFactory.getLogger(GitAdd.class);
	
	private Path gitRepoPath;
	
	public GitAdd(Path gitRepoPath) {
		this.gitRepoPath = gitRepoPath;
	}

	public void execute(String filePattern) {
		Path gitDir = gitRepoPath.resolve(Constants.DOT_GIT);
		try (Repository repo = FileRepositoryBuilder.create(gitDir.toFile());
			Git git = new Git(repo)){
			git.add().addFilepattern(filePattern).call();
		} catch (IOException e) {
			logger.error("仓库不存在", e);
			throw new GitRepoNotFoundException(gitRepoPath.toString());
		} catch (GitAPIException e) {
			logger.error("执行 git add 失败", e);
			throw new GitAddFailedException(e);
		} 
	}

}
