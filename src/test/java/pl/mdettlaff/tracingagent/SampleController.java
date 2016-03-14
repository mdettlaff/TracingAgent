package pl.mdettlaff.tracingagent;

public class SampleController {

	public static void main(String[] args) throws Exception {
		SampleController controller = new SampleController();
		while (true) {
			controller.init();
			controller.other();
			Thread.sleep(1000);
		}
	}

	public void init() throws InterruptedException {
		System.out.println("doing init stuff");
		Thread.sleep(1000);
		System.out.println("done init stuff");
	}

	public void other() throws InterruptedException {
		System.out.println("doing other stuff");
		Thread.sleep(1000);
		System.out.println("done other stuff");
	}
}
