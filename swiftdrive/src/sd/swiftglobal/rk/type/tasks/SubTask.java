package sd.swiftglobal.rk.type.tasks;

import sd.swiftglobal.rk.type.handler.HandleType;

public class SubTask implements HandleType {

	public static int TASK_COMPLETE  = 0,
		   			  TASK_PENDING   = 1,
					  TASK_CANCELLED = 2;

	private String name,
				   desc,
				   lead,
				   startdate,
				   finishdate,
				   rawdata;
	
	private int status   = TASK_PENDING,
				priority = 0,
				id;

	public SubTask(String rawdata) {
		this.rawdata = rawdata;
		toData();
	}
	
	public SubTask(String name, String desc, String lead, String start, String finish, int priority, int status) {
		this.name = name;
		this.desc = desc;
		this.lead = lead;
		startdate = start;
		finishdate = finish;
		this.priority = priority;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public void setID(int id) {
		this.id = id;
	}

	@Override
	public void toData() {
		String[] split = rawdata.split(";");
		if(split.length == 7) {
			name = split[0];
			desc = split[1];
			lead = split[2];
			startdate = split[3];
			finishdate = split[4];
			priority = Integer.parseInt(split[5]);
			status = Integer.parseInt(split[6]);
		}
	}

	@Override
	public String toString() {
		String d = ";";
		rawdata = name + d + desc + d + lead + d + startdate + d + finishdate + d + priority + d + status;
		return rawdata;
	}

	@Override
	public void setInfo(int id, String str) {
		this.id = id;
		rawdata = str;
	}

	public void setStatus(int s) {
		status = s;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String d) {
		desc = d;
	}

	public String getLead() {
		return lead;
	}

	public void setLead(String l) {
		lead = l;
	}

	public int getStatus() {
		return status;
	}

	public void setStartDate(String date) {
		startdate = date;
	}

	public String getStartDate() {
		return startdate;
	}

	public void setFinishDate(String date) {
		finishdate = date;
	}

	public String getFinishDate() {
		return finishdate;
	}
}
