package pl.jrj.data;

import javax.ejb.Remote;

@Remote
public interface IDataMonitor {

    boolean hasNext();

    double next();
}