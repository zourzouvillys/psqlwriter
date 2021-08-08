package io.zrz.psqlwriter;

import io.zrz.psqlwriter.execution.Query;
import io.zrz.psqlwriter.execution.QueryParameters;

/**
 * contains a pre-packaged SQL query along with parameters (if any).
 * 
 * @author theo
 *
 */

public class Tuple {

	private final Query query;
	private final QueryParameters params;

	private Tuple(Query query, QueryParameters params) {
		this.query = query;
		this.params = params;
	}

	public static Tuple of(Query query, QueryParameters params) {
		return new Tuple(query, params);
	}

	public Query query() {
		return this.query;
	}

	public QueryParameters params() {
		return this.params;
	}

}
