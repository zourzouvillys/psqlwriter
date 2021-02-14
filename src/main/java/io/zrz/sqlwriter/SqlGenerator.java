package io.zrz.sqlwriter;

/**
 *
 */

@FunctionalInterface
public interface SqlGenerator {

	void write(SqlWriter w);

//	default void addTo(final QueryExecutionBuilder qb) {
//		addTo(qb, false);
//	}
//

	default String asString() {
		final SqlWriter w = new SqlWriter(true);
		w.write(this);
		return w.toString();
	}

//
//	default Publisher<QueryResult> submitTo(final PostgresQueryProcessor pg) {
//		final SqlWriter w = new SqlWriter(false);
//		w.write(this);
//		return w.submitTo(pg);
//	}
//
//	default void addTo(final QueryExecutionBuilder qb, final boolean forceInline) {
//		final SqlWriter w = new SqlWriter(forceInline);
//		w.write(this);
//		w.addTo(qb);
//	}
//
//	default Tuple asTuple() {
//		return asTuple(false);
//	}
//	
//	default Tuple asTuple(final boolean forceInline) {
//		final SqlWriter w = new SqlWriter(forceInline);
//		w.write(this);
//		return w.createTuple();
//	}

}
