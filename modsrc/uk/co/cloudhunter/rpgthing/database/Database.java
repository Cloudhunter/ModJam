package uk.co.cloudhunter.rpgthing.database;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * I REGRET NOTHING!
 * 
 * @author AfterLifeLochie
 */
public class Database {

	private static final Class<?>[] packRules = { int.class, Integer.class, boolean.class, Boolean.class, double.class,
			Double.class, float.class, Float.class, String.class };

	private static int getPackRule(Class<?> clazz) {
		for (int i = 0; i < packRules.length; i++)
			if (clazz.equals(packRules[i]))
				return i;
		return -1;
	}

	private static Class<?> getUnpackRule(int k) {
		if (k >= 0 && k < packRules.length)
			return packRules[k];
		return null;
	}

	public static class Row {
		/**
		 * Value dec
		 */
		private HashMap<Integer, Object> values;
		private final int row_id;

		public Row(int id) {
			this.row_id = id;
		}

		public void put(int i, Object o) {
			values.put(i, o);
		}

		public Object get(int i) {
			return values.get(i);
		}

		public int id() {
			return row_id;
		}
	}

	public static class Table {

		/**
		 * Structure dec
		 */
		private HashMap<Integer, Class<?>> struct;
		/**
		 * Label dec
		 */
		private HashMap<Integer, String> labels;
		/**
		 * Value dec
		 */
		private HashMap<Integer, Row> values;
		/**
		 * Count field
		 */
		private int auto_inc;

		public Table() {
			this.struct = new HashMap<Integer, Class<?>>();
			this.labels = new HashMap<Integer, String>();
			this.values = new HashMap<Integer, Row>();
			this.auto_inc = 0;
		}

		/**
		 * Declares a field in the table
		 * 
		 * @param i
		 *            The ordinal
		 * @param val
		 *            The value typeof
		 * @param label
		 *            The label
		 */
		public void struct(int i, Class<?> val, String label) {
			if (getPackRule(val) == -1)
				throw new UnsupportedOperationException("Can't specify non-primitive field types, fail.");
			this.struct.put(i, val);
			this.labels.put(i, label);
		}

		/**
		 * Puts a row into the table
		 * 
		 * @param vals
		 *            All values
		 * @return The row ID which was inserted
		 */
		public int put(Object[]... vals) {
			int v = auto_inc++;
			Row row = new Row(v);
			if (vals.length != struct.size())
				throw new UnsupportedOperationException("Wrong length, fail.");
			for (int i = 0; i < vals.length; i++) {
				if (vals[i] != null && !vals[i].getClass().equals(struct.get(i)))
					throw new UnsupportedOperationException("Field value at " + i + " not matching typeof "
							+ struct.get(i).getName() + ", fail.");
				row.put(i, vals[i]);
			}
			values.put(v, row);
			return v;
		}

		/**
		 * Removes a row from the table; this doesn't reset auto increment rules
		 * 
		 * @param i
		 *            The row number to remove
		 * @return Number of affected rows
		 */
		public int remove(int i) {
			return (values.remove(i) != null) ? 1 : 0;
		}

		/**
		 * Gets a row from the table
		 * 
		 * @param j
		 *            The row number
		 * @return The row, or nothing
		 */
		public Row get(int j) {
			return values.get(j);
		}

		/**
		 * Return all results which match a value declared
		 * 
		 * @param field_num
		 *            The field number
		 * @param value
		 *            The value
		 * @param cap
		 *            Max value count to return
		 * @return All matching rows
		 */
		public ArrayList<Row> match(int field_num, Object value, int cap) {
			if (struct.get(field_num) == null)
				throw new UnsupportedOperationException("No such field, fail.");
			if (!value.getClass().equals(struct.get(field_num)))
				throw new UnsupportedOperationException("Search object " + value.getClass().getName()
						+ " not matching typeof " + struct.get(field_num).getName() + ", fail.");
			ArrayList<Row> results = new ArrayList<Row>();
			for (int i = 0; i < rows(); i++) {
				if (results.size() > cap)
					break;
				Row row = get(i);
				if (row.get(field_num).equals(value))
					results.add(row);
			}
			return results;
		}

		/**
		 * Map a label to row id
		 * 
		 * @param name
		 *            The name
		 * @return The id
		 */
		public int map(String name) {
			for (int i = 0; i < labels.size(); i++)
				if (labels.get(i).equalsIgnoreCase(name))
					return i;
			return -1;
		}

		/**
		 * Map an id to a label
		 * 
		 * @param i
		 *            The ID
		 * @return The name
		 */
		public String map(int i) {
			return labels.get(i);
		}

		/**
		 * The number of rows in the table
		 * 
		 * @return The number of rows
		 */
		public int rows() {
			return values.size();
		}

		/**
		 * The number of non-empty rows in the table; this is costly, so use sparingly
		 * 
		 * @return The number of nonempty rows
		 */
		public int nonEmptyRows() {
			int result = 0;
			for (int i = 0; i < values.size(); i++)
				if (values.get(i) != null)
					result++;
			return result;
		}

		/**
		 * Save the table
		 * 
		 * @param stream
		 *            The stream
		 * @throws IOException
		 *             The boo-boo
		 */
		public void save(OutputStream stream) throws IOException {
			DataOutputStream out = new DataOutputStream(stream);
			out.writeInt(rows());
			out.writeInt(auto_inc);
			for (int i = 0; i < rows(); i++) {
				Row row = get(i);
				if (row != null) {
					out.writeInt(i);
					for (int j = 0; j < struct.size(); j++) {
						Object value = row.get(j);
						if (value == null)
							out.writeInt(-1);
						else {
							int typeof = getPackRule(value.getClass());
							if (typeof == -1)
								throw new UnsupportedOperationException("Can't pack non primitive type, fail.");
							switch (typeof) {
								case 0:
								case 1:
									out.writeInt((Integer) value);
									break;
								case 2:
								case 3:
									out.writeByte((Boolean) value ? 1 : 0);
									break;
								case 4:
								case 5:
									out.writeDouble((Double) value);
									break;
								case 6:
								case 7:
									out.writeFloat((Float) value);
									break;
								case 8:
									String s = (String) value;
									out.writeInt(s.length());
									for (char c : s.toCharArray())
										out.writeChar(c);
									break;
								default:
									throw new UnsupportedOperationException("Strange primitive type, fail.");
							}
						}
					}
				}
			}
		}

		/**
		 * Load the table
		 * 
		 * @param stream
		 *            The stream
		 * @throws IOException
		 *             The boo-boo
		 */
		public void load(InputStream stream) throws IOException {
			DataInputStream in = new DataInputStream(stream);
			int num_rows = in.readInt();
			auto_inc = in.readInt();
			for (int i = 0; i < num_rows; i++) {
				int row_id = in.readInt();
				Row row = new Row(row_id);
				for (int j = 0; j < struct.size(); j++) {
					int typeof = in.readInt();
					if (typeof == -1)
						row.put(j, null);
					else {
						Class<?> of = getUnpackRule(typeof);
						if (of.equals(int.class) || of.equals(Integer.class))
							row.put(j, in.readInt());
						else if (of.equals(boolean.class) || of.equals(Boolean.class))
							row.put(j, (in.readByte() != 0));
						else if (of.equals(double.class) || of.equals(Double.class))
							row.put(j, in.readDouble());
						else if (of.equals(float.class) || of.equals(Float.class))
							row.put(j, in.readFloat());
						else if (of.equals(String.class)) {
							int length = in.readInt();
							StringBuilder result = new StringBuilder();
							for (int k = 0; k < length; k++)
								result.append(in.readChar());
							row.put(j, result.toString());
						} else
							throw new UnsupportedOperationException("Strange primitive type, fail.");
					}
				}
				values.put(row_id, row);
			}
		}
	}

	/**
	 * Map of all tables
	 */
	private HashMap<String, Table> tables;

	public Database() {
		this.tables = new HashMap<String, Database.Table>();
	}

	/**
	 * Creates a blank table
	 * 
	 * @param name
	 *            The name
	 * @return The table result
	 */
	public Table create(String name) {
		tables.put(name, new Table());
		return tables.get(name);
	}

	/**
	 * Gets a table
	 * 
	 * @param name
	 *            The name
	 * @return The table result
	 */
	public Table get(String name) {
		return tables.get(name);
	}

	/**
	 * Removes a table
	 * 
	 * @param name
	 *            The name
	 * @return Affected result count
	 */
	public int remove(String name) {
		return (tables.remove(name) != null) ? 1 : 0;
	}

	/**
	 * Save the database state, this will dump all tables to the outputstream specified.
	 * 
	 * @param stream
	 *            The outputstream
	 * @throws IOException
	 *             If something went wrong
	 */
	public void save(OutputStream stream) throws IOException {
		DataOutputStream out = new DataOutputStream(stream);
		out.writeInt(tables.size());
		for (Entry<String, Table> table : tables.entrySet()) {
			out.writeInt(table.getKey().length());
			for (char c : table.getKey().toCharArray())
				out.writeChar(c);
			table.getValue().save(stream);
		}
	}

	/**
	 * Load the database state. The tables all have to exist already, so they should be
	 * statically declared and setup (including schema)
	 * 
	 * @param stream
	 *            The inputstream
	 * @throws IOException
	 *             If something went wrong
	 */
	public void load(InputStream stream) throws IOException {
		DataInputStream in = new DataInputStream(stream);
		int table_count = in.readInt();
		for (int i = 0; i < table_count; i++) {
			int j = in.readInt();
			StringBuilder result = new StringBuilder();
			for (int k = 0; k < j; k++)
				result.append(in.readChar());
			get(result.toString()).load(stream);
		}
	}

}
