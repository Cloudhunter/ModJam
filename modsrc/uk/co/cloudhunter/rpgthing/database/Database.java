package uk.co.cloudhunter.rpgthing.database;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class Database {

	public static class Table {
		private HashMap<Integer, Class<?>> struct;
		private HashMap<Integer, String> labels;
		private HashMap<Integer, HashMap<Integer, Object>> values;
		private int auto_inc;

		public Table() {
			this.struct = new HashMap<Integer, Class<?>>();
			this.labels = new HashMap<Integer, String>();
			this.values = new HashMap<Integer, HashMap<Integer, Object>>();
			this.auto_inc = 0;
		}

		public void struct(int i, Class<?> val, String label) {
			this.struct.put(i, val);
			this.labels.put(i, label);
		}

		public int put(Object[]... vals) {
			int v = auto_inc++;
			HashMap<Integer, Object> row = new HashMap<Integer, Object>();
			if (vals.length != struct.size())
				throw new UnsupportedOperationException("Wrong length, fail.");
			for (int i = 0; i < vals.length; i++) {
				// Allow null values, just in case
				if (vals[i] != null && !vals[i].getClass().equals(struct.get(i)))
					throw new UnsupportedOperationException("Field value at " + i + " not matching typeof "
							+ struct.get(i).getName() + ", fail.");
				row.put(i, vals[i]);
			}
			return v;
		}

		public int remove(int i) {
			return (values.remove(i) != null) ? 1 : 0;
		}

		public HashMap<Integer, Object> get(int j) {
			return values.get(j);
		}

		public int rows() {
			return values.size();
		}

		public void save(OutputStream stream) throws IOException {
			DataOutputStream out = new DataOutputStream(stream);
			out.writeInt(rows());
			out.writeInt(auto_inc);
			for (int i = 0; i < rows(); i++) {
				HashMap<Integer, Object> row = get(i);
				if (row != null) {
					out.writeInt(i);
					for (int j = 0; j < struct.size(); j++) {

					}
				}
			}
		}

		public void load(InputStream stream) throws IOException {
			DataInputStream in = new DataInputStream(stream);
		}
	}

}
