import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.http.HTTPRepository;


public class TaxonomyTreeCreator {

	private static final String DIRECT_SUBCLASSES_QUERY =
		"PREFIX sesame: <http://www.openrdf.org/schema/sesame#>\n" +
		"SELECT DISTINCT ?subs WHERE { \n"
		+ "?subs sesame:directSubClassOf <%s> .\n" +
		"}";
	
	
	public static void main(String[] args) throws Exception {
		HTTPRepository repository = new HTTPRepository("http://192.168.130.248:8080/graphdb-workbench", "pub");
		final RepositoryConnection connection = repository.getConnection();

		String root = "http://ontology.ontotext.com/taxonomy/Thing";
		System.out.println(root);

		DirectSubclassesHandler rootHandler = new DirectSubclassesHandler(connection, "    ", 0);
		
		TupleQuery tq = 
			connection.prepareTupleQuery(QueryLanguage.SPARQL, 
					String.format(DIRECT_SUBCLASSES_QUERY, root));
		
		tq.evaluate(rootHandler);
		
		connection.close();
	}
	
	protected static class DirectSubclassesHandler implements TupleQueryResultHandler {

		private RepositoryConnection conn;
		private String delim;
		private int lev;
		
		public DirectSubclassesHandler(RepositoryConnection connection, String delimiter, int level) {
			this.conn = connection;
			this.delim = delimiter;
			this.lev = level;
		}
		
		@Override
		public void handleBoolean(boolean value) throws QueryResultHandlerException {}

		@Override
		public void handleLinks(List<String> linkUrls) throws QueryResultHandlerException {}

		@Override
		public void startQueryResult(List<String> bindingNames) throws TupleQueryResultHandlerException {}
		
		@Override
		public void endQueryResult() throws TupleQueryResultHandlerException {
		}

		@Override
		public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
			String sub = bindingSet.getValue("subs").stringValue();
			System.out.println(StringUtils.repeat(delim, lev) + "|- " + sub);
			
			DirectSubclassesHandler handler = new DirectSubclassesHandler(conn, delim, lev + 1);
			try {
				TupleQuery query = conn.prepareTupleQuery(QueryLanguage.SPARQL, String.format(DIRECT_SUBCLASSES_QUERY, sub));
				query.evaluate(handler);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}
}

