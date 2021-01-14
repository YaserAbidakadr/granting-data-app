package ca.gc.tri_agency.granting_data.form;

import java.util.LinkedList;
import java.util.stream.IntStream;

public class FormErrorCountIterator {
	
	private LinkedList<Integer> errCounterQueue;
	
	public FormErrorCountIterator(int numErrs) {
		errCounterQueue = new LinkedList<>();
		IntStream.range(1, numErrs + 1).forEach(errCounterQueue::add);
	}
	
	public int getNextErrNum() {
		return errCounterQueue.poll();
	}
	
	public int getQueueSize() {
		return errCounterQueue.size();
	}

}
