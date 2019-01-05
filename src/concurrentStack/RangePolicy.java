package concurrentStack;

// Original code

public class RangePolicy {
	
	private int _returnRange, _fullRange, _successes, _failures, _timeConstricted, _limit;
	private double _ratio;
	static int LIMIT = 100;
	
	public RangePolicy(int fullRange) {
		_fullRange = _returnRange = fullRange;
		_ratio = _successes = _failures = _timeConstricted = 0;
		_limit = LIMIT;
	}
	
	public int getRange() {
		// all the other methods help determine what this number actually is.  Initially it's the full range. 
		return _returnRange;
	}
	
	public void recordEliminationSuccess() throws OversizedExchangerArray {
		_successes++;
		recalculate();
	}
	
	public void recordEliminationTimeout() throws OversizedExchangerArray {
		_failures++;
		recalculate();
	}
	
	private void recalculate() throws OversizedExchangerArray {
		if (_successes + _failures >= _limit) {
			int diff = _successes - _failures;
			boolean moreSuccessesThanFailures = (diff < 0) ? false : true; // record the sign
			diff = Math.abs(diff);
			if (diff < LIMIT) {
				if (diff >= 50) { //so its a 75-25 split between failures and successes or vice versa
					if (!moreSuccessesThanFailures) { // if 75% failure rate
						_failures = diff; // it's important to keep because something needs to be done
						_successes = 0;
					}
					// if it is a 75% percent success rate, it is fine to start over at zero
				} else if (diff < 25 || diff < _fullRange / 10) { // if there is not too extreme an inequality absolutely or relatively
					if (moreSuccessesThanFailures) { // just let the victor keep it
						_successes = diff;
					} else {
						_failures = diff;
					}
				} else { // otherwise, reset
					_successes = _failures = 0;
				}
			} else {
				_limit = diff + 1;
				throw new OversizedExchangerArray();
			}
		}
		if (_successes == 0) {
			_ratio = _failures;
		} else {
			_ratio = _failures / _successes;
		}
		if (_timeConstricted > 5) { // the range can't be allowed to just get smaller permanently
			_timeConstricted--;
			_returnRange *= 2;
			if (_returnRange > _fullRange) {
				_returnRange = _fullRange;
			}
		} else if (_ratio > 0.33) { // 0.33 = 25/75
			// if there is high failure, restrict the range absolutely or relativel
			if (_returnRange > 20 || _returnRange > _fullRange / 10) {
				_returnRange /= 2;
			}
		} else { // count the time restricted
			_timeConstricted++;
		}
		
	}

}
	

