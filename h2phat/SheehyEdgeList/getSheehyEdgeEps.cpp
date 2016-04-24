void getEdge(int p, int q, double* X, int N, int D, double* ts, int* e1, int* e2, double* ed, double eps) {
	double dpq = sqrt(getSqrDist(X, N, D, p, q));
	double tp = ts[p];
	double tq = ts[q];
	double alpha;
	
	//SEE SHEEHY PAGE 8
	if (tq < tp) {
		//Make sure tp indexes the earlier of the two death times
		double temp = tq;
		tq = tp;
		tp = temp;
	}
	//Case a (no warping has occurred yet)
	if (dpq < tp*(1-2*eps)) {
		*e1 = p;
		*e2 = q;
		*ed = dpq;
		return;
	}
	//Case b: alpha is between (1-2eps)tp and tp but is less than (1-2eps)tq
	alpha = 2*dpq - (1-2*eps)*tp;
	if (alpha >= (1-2*eps)*tp && alpha <= tp && alpha <= (1-2*eps)*tq) {
		*e1 = p;
		*e2 = q;
		*ed = alpha;
		return;
	}
	
	//Otherwise, it's never added
	*e1 = -1;
	*e2 = -1;
}
