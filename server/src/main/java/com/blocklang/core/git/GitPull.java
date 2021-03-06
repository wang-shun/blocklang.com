package com.blocklang.core.git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.TagOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blocklang.core.git.exception.GitPullFailedException;
import com.blocklang.core.git.exception.GitRepoNotFoundException;

public class GitPull {

	private static final Logger logger = LoggerFactory.getLogger(GitPull.class);
	
	public boolean execute(Path gitRepoPath, boolean fetchTag){
		File directory = gitRepoPath.resolve(Constants.DOT_GIT).toFile();
		try (Repository repository = FileRepositoryBuilder.create(directory); 
				Git git = new Git(repository)){
			PullCommand pc = git.pull();
			if(fetchTag) {
				pc.setTagOpt(TagOpt.FETCH_TAGS);
			}
			PullResult pullResult = pc.call();
			if(pullResult.isSuccessful()){
				logger.info("git pull success");
			}else{
				logger.error("git pull failed");
			}
			return pullResult.isSuccessful();
		} catch (GitAPIException e) {
			throw new GitPullFailedException(e);
		} catch (IOException e) {
			throw new GitRepoNotFoundException(gitRepoPath.toString());
		}
	}
	
}
