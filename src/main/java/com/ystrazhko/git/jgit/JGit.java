package com.ystrazhko.git.jgit;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 *
 *
 * @author Lyska Lyudmila
 */
public class JGit {

    private static final JGit _jgit;
    private Git _git;
    private CredentialsProvider _credentials;
    private boolean _isRememberCredentials;

    static {
        _jgit = new JGit();
    }

    private JGit() {
        _credentials = null;
    }

    public static JGit getInstance() {
        return _jgit;
    }

    public boolean createRepository(String localPath) {
        try {
            Repository rep = new FileRepository(localPath + "/.git");
            rep.create();
            _git = new Git(rep);
            return true;
        } catch (IOException e) {
            System.err.println("!ERROR: " + e.getMessage());
            return false;
        }
    }

    public boolean clone(String linkClone, String localPath) {
        if (!createRepository(localPath)) {
            return false;
        }

        try {
            Git.cloneRepository().setURI(linkClone).setDirectory(new File(localPath)).call();
            return true;
        } catch (InvalidRemoteException | TransportException e) {
            System.err.println("!ERROR: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    public boolean commit(String linkClone, String localPath, String message) {
        try {
            _git.commit().setMessage(message).call();
            return true;
        } catch (NoHeadException | NoMessageException | UnmergedPathsException
                        | ConcurrentRefUpdateException| WrongRepositoryStateException e) {
            System.err.println("!ERROR: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }

    public boolean commitAndPush(String linkClone, String localPath, String message) {
        if (!commit(linkClone, localPath, message)) {
            return false;
        }
        return push();
    }

    public boolean commitAndPush(String linkClone, String localPath, String message,
                                 String login, String password, boolean isRemember) {
        if (!commit(linkClone, localPath, message)) {
            return false;
        }
        return push();
    }

    public boolean push() {
        return push(null, null, false);
    }

    public boolean push(String login, String password, boolean isRemember) {
        //use stored credentials
        if (_isRememberCredentials) {
            return pushWithCredentials(_credentials);
        }

        if (login == null && password == null) {
            return false;
        }

        if(isRemember) {
            // if need to store new credentials
            _isRememberCredentials = isRemember;
            _credentials = new UsernamePasswordCredentialsProvider(login, password);
            return pushWithCredentials(_credentials);
        } else {
            return pushWithCredentials(new UsernamePasswordCredentialsProvider(login, password));
        }
    }

    private boolean pushWithCredentials(CredentialsProvider credentials) {
        try {
            _git.push().setCredentialsProvider( credentials ).call();
            return true;
        } catch (InvalidRemoteException | TransportException e) {
            System.err.println("!ERROR: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
        return false;
    }
}
