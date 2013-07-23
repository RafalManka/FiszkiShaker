package pl.rafalmanka.fiszki.shaker.animations;

import android.view.View;

public class FlipcardAnimation implements Runnable {

	final Thread animator;

	public FlipcardAnimation(final View rootLayout, final View cardFace,
			final View cardBack) {

		animator = new Thread(new Runnable() {

			@Override
			public void run() {

				FlipAnimation flipAnimation = new FlipAnimation(cardFace,
						cardBack);

				if (cardFace.getVisibility() == View.GONE) {
					flipAnimation.reverse();
				}
				rootLayout.startAnimation(flipAnimation);

			}
		});

	}

	public void startAnimation() {
		animator.start();
	}

	public void awaitCompletion() throws InterruptedException {
		animator.join();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
