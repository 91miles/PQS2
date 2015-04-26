package edu.nyu.pqs.stopwatch.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import edu.nyu.pqs.stopwatch.api.IStopwatch;

enum status {
  stopped, running;
}
public class StopWatch implements IStopwatch {

  /** use a logger instead of System.out.println */
  private static final Logger logger = 
      Logger.getLogger("edu.nyu.pqs.ps2.impl.StopWatch");

  /**
   * identifier of stopwatch
   */ 
  private String Id; 
  private status status;
  private List<Long> lapTimes;
  private long startTime;
  private long lapTime;

  private Object lock;
  /** 
   * constructor 
   * @param a string, id
   */
  public StopWatch(String id) {
    this.Id = id; 
    this.status = status.stopped;
    this.lapTimes = new ArrayList<Long>();
    this.startTime=0L;
    this.lock = new Object();
  }

  /**
   * Returns the Id of this stopwatch
   * @return the Id of this stopwatch.  Will never be empty or null.
   */
  @Override
  public String getId() {
    return this.Id;
  }

  /**
   * Starts the stopwatch.
   * @throws IllegalStateException if called when the stopwatch is already running
   */
  @Override
  public void start() {
    synchronized (lock) {
      if (this.status == status.running) {
        throw new IllegalStateException("The stopwatch is already running");
      }
      Long currTime = System.currentTimeMillis();
      // if the stopwatch was stopped
      startTime = currTime;
      this.status=status.running;
    }

  }

  /**
   * Stores the time elapsed since the last time lap() was called
   * or since start() was called if this is the first lap, 
   * and store the time to the lap time list. 
   * @throws IllegalStateException if called when the stopwatch isn't running
   */
  @Override
  public void lap() {
    synchronized (lock) {
      if (this.status == status.stopped){
        throw new IllegalStateException("The stopwatch is not running");
      }
      Long currTime = System.currentTimeMillis();
      lapTime = currTime - startTime;
      lapTimes.add(lapTime);
      startTime = currTime;
    }
  }

  /**
   * Stops the stopwatch (and records one final lap).
   * @throws IllegalStateException if called when the stopwatch isn't running
   */
  @Override
  public void stop() {
    synchronized (lock) {
      if (status != status.running) {
        throw new IllegalStateException();
      }
      Long currTime = System.currentTimeMillis();
      lapTime = currTime - startTime;
      lapTimes.add(lapTime);
      status = status.stopped; 
    }
  }

  /**
   * Resets the stopwatch.  If the stopwatch is running, this method stops the
   * watch and resets it.  This also clears all recorded laps.
   */
  @Override
  public void reset() {
    synchronized (lock) {
      status=status.stopped;
      lapTimes = new ArrayList<Long>();
      // status = status.stopped; 
      startTime = 0L;
      lapTime = 0L;
    }
  }

  /**
   * Returns a list of lap times (in milliseconds).  This method can be called at
   * any time and will not throw an exception.
   * @return a list of recorded lap times or an empty list if no times are recorded.
   */
  @Override
  public List<Long> getLapTimes() {
    return this.lapTimes;
  }

  /**
   * override the method to compare two objects of StopWatch
   */
  @Override
  public boolean equals (Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null) {
      return false;
    }

    if (obj.getClass() != getClass()) {
      return false;
    }

    StopWatch other = (StopWatch) obj;

    if ( !other.Id.equals(this.Id)) {
      return false;
    }

    if ( !other.status.equals(this.status) ) {
      return false;
    }

    if (!getLapTimes().equals(other.getLapTimes())) {
      return false;
    }

    return true;
  }

  /**
   * Returns the hashCode value for this stopwatch.
   */
  @Override
  public int hashCode() {
    int result = 57;
    result = 13 * result + this.Id.hashCode();
    result = 13 * result + this.status.hashCode();
    synchronized (lock) {
      result = 13 * result + lapTimes.hashCode();
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.Id+" is ");
    if (this.status ==status.running) {
      sb.append("running");
    }else {
      sb.append("stopped");
    }
    sb.append(" : [");

    synchronized (lock) {
      Iterator<Long> itr = getLapTimes().iterator();
      while (itr.hasNext()) {
        Long duration = itr.next();
        sb.append(String.valueOf(duration));
        if (itr.hasNext()) {
          sb.append(", ");
        }
      }
    }

    sb.append("]");
    return sb.toString();
  }

}
