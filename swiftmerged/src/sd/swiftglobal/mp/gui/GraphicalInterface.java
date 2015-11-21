package sd.swiftglobal.mp.gui;

import javax.swing.JPanel;

import sd.swiftglobal.rk.Meta.Ryan;
import sd.swiftglobal.rk.util.SwiftNet.SwiftNetContainer;

@Ryan
public class GraphicalInterface {
	public interface SwiftMaster {
		public void setContainer(SwiftContainer c);
		public SwiftContainer getContainer();

		public void setNetContainer(SwiftNetContainer c);
		public SwiftNetContainer getNetContainer();
	}

	public interface SwiftContainer {
		public void setPanel(SwiftPanel p);
		public SwiftPanel getCurrentPanel();

		public void setMaster(SwiftMaster m);
		public SwiftMaster getMaster();
	}

	public interface SwiftPanel {
		public void setParentContainer(SwiftContainer c);
		public SwiftContainer getParentContainer();

		public abstract JPanel getPanel();
	}
}
