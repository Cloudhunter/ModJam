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

		public void load(InputStream stream) throws IOException {
			DataInputStream in = new DataInputStream(stream);
			int num_rows = in.readInt();
			auto_inc = in.readInt();
			for (int i = 0; i < num_rows; i++) {
				HashMap<Integer, Object> row = new HashMap<Integer, Object>();
				int row_id = in.readInt();
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
			}
		}
	}

}
