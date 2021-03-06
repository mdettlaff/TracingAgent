package pl.mdettlaff.tracingagent;

import javax.annotation.Resource;

@Resource
public class SampleController {

	public static void main(String[] args) throws Exception {
		SampleController controller = new SampleController();
		for (int i = 0; i < 5; i++) {
			controller.init();
			controller.other();
			Thread.sleep(100);
		}
	}

	public void init() throws InterruptedException {
		System.out.println("doing init stuff");
		Thread.sleep(100);
		System.out.println("done init stuff");
	}

	public void other() throws InterruptedException {
		System.out.println("doing other stuff");
		Thread.sleep(100);
		System.out.println("done other stuff");
	}
}
