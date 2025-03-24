package kr.co.triphos.common.service;


import org.json.simple.JSONObject;

public class JSON extends JSONObject {
	/***
	 * JSON Key에 해당되는 값 가져오기
	 */
	public String getString(String name) {
		String value = "";

		try {
			value = this.get(name).toString();
		}
		catch (Exception ignored) {}

		return value;
	}

	public int getInt(String name) {
		int value = 0;

		try {
			value = Integer.parseInt(this.get(name).toString());
		}
		catch (Exception ignored) {}

		return value;
	}

	public long getLong(String name) {
		long value = 0;

		try {
			value = Long.parseLong(this.get(name).toString());
		}
		catch (Exception ignored) {}

		return value;
	}

	public float getFloat(String name) {
		float value = 0;

		try {
			value = Float.parseFloat(this.get(name).toString());
		}
		catch (Exception ignored) {}

		return value;
	}

	public double getDouble(String name) {
		double value = 0;

		try {
			value = Double.parseDouble(this.get(name).toString());
		}
		catch (Exception ignored) {}

		return value;
	}

	public boolean getBoolean(String name) {
		boolean value = false;

		try {
			value = (boolean) this.get(name);
		}
		catch (Exception ignored) {}

		return value;
	}
}
