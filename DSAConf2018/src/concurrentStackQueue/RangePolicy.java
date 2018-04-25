package concurrentStackQueue;

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
			boolean moreSuccessesThanFailures = (diff < 0) ? false : true;
			diff = Math.abs(diff);
			if (diff < LIMIT) {
				if (diff >= 50) {
					if (!moreSuccessesThanFailures) {
						_failures = diff;
						_successes = 0;
					}
				} else if (diff < 25 || diff < _fullRange / 10) {
					if (moreSuccessesThanFailures) {
						_successes = diff;
					} else {
						_failures = diff;
					}
				} else {
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
		if (_timeConstricted > 5) {
			_timeConstricted--;
			_returnRange *= 2;
			if (_returnRange > _fullRange) {
				_returnRange = _fullRange;
			}
		} else if (_ratio > 0.25) {
			if (_returnRange > 20 || _returnRange > _fullRange / 10) {
				_returnRange /= 2;
			}
		} else {
			_timeConstricted++;
		}
		
	}

}
	

