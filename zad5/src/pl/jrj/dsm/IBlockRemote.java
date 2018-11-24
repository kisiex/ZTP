package pl.jrj.dsm;

import javax.ejb.Remote;
import java.util.List;

/**
 * @author Adam Kisielewski
 * @version 0.1-SNAPSHOT
 */

@Remote
public interface IBlockRemote {
    public double calcVolume(List<Solver.Point> pointList);
}
