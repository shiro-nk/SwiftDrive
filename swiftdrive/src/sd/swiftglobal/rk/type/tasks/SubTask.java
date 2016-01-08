package sd.swiftglobal.rk.type.tasks;

import sd.swiftglobal.rk.type.handler.HandleType;

/**
 * <b>Subtask:</b><br>
 * Subtasks that contain information about the progress, leader,
 * due dates/schedualing, priority and name of a subtask. 
 *
 * @author Ryan Kerr
 */
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

	/**
	 * <b>Constructor:</b><br>
	 * Create a subtask from filedata
	 *
	 * @param rawdata Data from file
	 */
	public SubTask(String rawdata) {
		this.rawdata = rawdata;
		toData();
	}
	
	/**
	 * <b>Constructor:</b><br>
	 * Create a subtask with all its information.
	 *
	 * @param name SubTask name
	 * @param desc Description
	 * @param lead The person in charge of the subtask
	 * @param start The starting date of this subtask (dd/mm/yy)
	 * @param finish The date the subtask is due to be completed
	 * @param priority The priority of the task
	 * @param status The status of the task (using TASK_(STATUS))
	 */
	public SubTask(String name, String desc, String lead, String start, String finish, int priority, int status) {
		this.name = name;
		this.desc = desc;
		this.lead = lead;
		startdate = start;
		finishdate = finish;
		this.priority = priority;
		this.status = status;
	}

	/** @param s new status (use TASK_ COMPLETE/CANCELLED/PENDING **/
	public void setStatus(int s) {
		status = s;
	}

	/** @return Short description / metadata for the subtask **/
	public String getDesc() {
		return desc;
	}

	/** @param d new subtask description or metadata (very short) **/
	public void setDesc(String d) {
		desc = d;
	}

	/** @return Gets the screen name of the task lead **/
	public String getLead() {
		return lead;
	}

	/** @param l Name of the task lead **/
	public void setLead(String l) {
		lead = l;
	}

	/** @return Get TASK_STATUS **/
	public int getStatus() {
		return status;
	}

	/** @param date set the start date (dd/mm/yy format) **/
	public void setStartDate(String date) {
		startdate = date;
	}

	/** @return get start date in the dd/mm/yy format **/
	public String getStartDate() {
		return startdate;
	}

	/** @param date set due date (dd/mm/yy format) **/
	public void setFinishDate(String date) {
		finishdate = date;
	}

	/** @return due date in dd/mm/yy format **/
	public String getFinishDate() {
		return finishdate;
	}

	/** @return Task priority (0: Low, 1: Normal, 2: High) **/
	public int getPriority() {
		return priority;
	}

	/** @param p Set task priority **/
	public void setPriority(int p) {
		priority = p;
	}

	@Override
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
}
