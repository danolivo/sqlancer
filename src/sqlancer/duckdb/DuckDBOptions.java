package sqlancer.duckdb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import sqlancer.CompositeTestOracle;
import sqlancer.MainOptions.DBMSConverter;
import sqlancer.TestOracle;
import sqlancer.duckdb.DuckDBProvider.DuckDBGlobalState;
import sqlancer.duckdb.test.DuckDBNoRECOracle;
import sqlancer.duckdb.test.DuckDBQueryPartitioningAggregateTester;
import sqlancer.duckdb.test.DuckDBQueryPartitioningDistinctTester;
import sqlancer.duckdb.test.DuckDBQueryPartitioningHavingTester;
import sqlancer.duckdb.test.DuckDBQueryPartitioningWhereTester;

@Parameters
public class DuckDBOptions {
	
	@Parameter(names = "--test-collate")
	public boolean testCollate = true;
	
	@Parameter(names = "--test-check", description="Allow generating CHECK constraints in tables")
	public boolean testCheckConstraints = true;
	
	@Parameter(names = "--test-default-values", description="Allow generating DEFAULT values in tables")
	public boolean testDefaultValues = true;
	
	@Parameter(names = "--test-functions", description="Allow generating functions in expressions")
	public boolean testFunctions = true;
	
	@Parameter(names = "--test-functions", description="Allow generating casts in expressions")
	public boolean testCasts = true;
	
	@Parameter(names = "--test-between", description="Allow generating the BETWEEN operator in expressions")
	public boolean testBetween = true;
	
	@Parameter(names = "--test-in", description="Allow generating the IN operator in expressions")
	public boolean testIn = true;
	
	@Parameter(names = "--test-case", description="Allow generating the CASE operator in expressions")
	public boolean testCase = true;
	
	@Parameter(names = "--test-binary-logicals", description="Allow generating AND and OR in expressions")
	public boolean testBinaryLogicals = true;
	
	@Parameter(names = "--test-binary-comparisons", description="Allow generating binary comparison operators (e.g., >= or LIKE)")
	public boolean testBinaryComparisons = true;
	
	@Parameter(names = "--oracle", converter = DBMSConverter.class)
	public List<DuckDBOracle> oracle = Arrays.asList(DuckDBOracle.QUERY_PARTITIONING);

	public static enum DuckDBOracle {
		NOREC {

			@Override
			public TestOracle create(DuckDBGlobalState globalState) throws SQLException {
				return new DuckDBNoRECOracle(globalState);
			}
			
		},
		HAVING {
			@Override
			public TestOracle create(DuckDBGlobalState globalState) throws SQLException {
				return new DuckDBQueryPartitioningHavingTester(globalState);
			}
		},
		WHERE {
			@Override
			public TestOracle create(DuckDBGlobalState globalState) throws SQLException {
				return new DuckDBQueryPartitioningWhereTester(globalState);
			}
		},
		AGGREGATE {

			@Override
			public TestOracle create(DuckDBGlobalState globalState) throws SQLException {
				return new DuckDBQueryPartitioningAggregateTester(globalState);
			}
			
		},
		DISTINCT {
			@Override
			public TestOracle create(DuckDBGlobalState globalState) throws SQLException {
				return new DuckDBQueryPartitioningDistinctTester(globalState);
			}
		},	
		QUERY_PARTITIONING {
			@Override
			public TestOracle create(DuckDBGlobalState globalState) throws SQLException {
				List<TestOracle> oracles = new ArrayList<>();
				oracles.add(new DuckDBQueryPartitioningWhereTester(globalState));
				oracles.add(new DuckDBQueryPartitioningHavingTester(globalState));
				oracles.add(new DuckDBQueryPartitioningAggregateTester(globalState));
				return new CompositeTestOracle(oracles);
			}
		};

		public abstract TestOracle create(DuckDBGlobalState globalState) throws SQLException;

	}


}