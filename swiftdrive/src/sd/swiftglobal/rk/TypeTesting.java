package sd.swiftglobal.rk;

import sd.swiftglobal.rk.type.tasks.SubTask;
import sd.swiftglobal.rk.type.tasks.Task;
import sd.swiftglobal.rk.type.tasks.TaskHandler;
import sd.swiftglobal.rk.type.users.User;
import sd.swiftglobal.rk.type.users.UserHandler;

public class TypeTesting {
	public static void main(String[] args) {
		try {
			UserHandler userhandler = new UserHandler();
			userhandler.add(new User("Xel", "data"));
			userhandler.remove(new User("Xel", "data"));
			userhandler.add(new User("example", "Mohan"));
			TaskHandler tx = new TaskHandler(userhandler);
			Task t = new Task("excel", userhandler);
			tx.add(t);
			t.add(new SubTask("A;A;A;A;A;0;0"));
			t.add(new SubTask("B;B;B;B;B;1;0"));
			t.add(new SubTask("C;C;C;C;C;2;0"));
			t.add(new SubTask("D;D;D;D;D;3;0"));
			t.add(new SubTask("E;E;E;E;E;4;0"));
			t.remove(new SubTask("C;C;C;C;C;2;0"));
			tx.remove(t);
		}
		catch(Exception fx) {
			fx.printStackTrace();
		}
	}
}
