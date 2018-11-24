package pl.jrj.dsm;

import javax.ejb.Remote;

/**
 * @author Adam Kisielewski
 * @version 0.1-SNAPSHOT
 */

@Remote
public interface IDSManagerRemote {
    public String getDS();
}