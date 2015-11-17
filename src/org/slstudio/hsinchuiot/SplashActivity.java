package org.slstudio.hsinchuiot;

import java.io.File;

import org.slstudio.hsinchuiot.model.User;
import org.slstudio.hsinchuiot.service.IOTException;
import org.slstudio.hsinchuiot.service.LoginService;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.SessionService;
import org.slstudio.hsinchuiot.ui.ISplashImageProvider;
import org.slstudio.hsinchuiot.ui.SplashImageProviderManager;
import org.slstudio.hsinchuiot.util.ImageUtil;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashActivity extends BaseActivity {

	private final int SPLASH_DISPLAY_LENGTH = 3000;
	private ISplashImageProvider splashImageProvider = null;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		splashImageProvider = SplashImageProviderManager.getInstance(this)
				.getSplashImageProvider(SplashImageProviderManager.RANDOM_PROVIDER);

		final ImageView image = (ImageView) findViewById(R.id.splash_image);
		Drawable splashImage = splashImageProvider.getSplashImage();
		if (splashImage != null) {
			image.setBackgroundDrawable(splashImage);
		}

		final AnimationSet as = new AnimationSet(false);

		final Animation ani1 = new ScaleAnimation(1f, 1.1f, 1f, 1.1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
				ScaleAnimation.RELATIVE_TO_SELF, 0.3f);
		final Animation ani2 = new AlphaAnimation(1.0f, 0.3f);

		as.addAnimation(ani1);
		as.addAnimation(ani2);

		ani1.setDuration(SPLASH_DISPLAY_LENGTH);
		ani1.setFillAfter(true);
		ani1.setStartOffset(500);

		ani2.setDuration(SPLASH_DISPLAY_LENGTH);
		ani2.setFillAfter(true);
		ani2.setStartOffset(2000);

		// as.setFillAfter(true);

		// RelativeLayout layer =
		// (RelativeLayout)findViewById(R.id.splash_layer);
		image.setAnimation(as);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// show progress dialog in UI thread
				showProgressDialog();
				// start new thread for handle the login
				new Thread(new Runnable() {

					@Override
					public void run() {
						prepare();
						login();
						//showDebugActivity(Constants.Action.HSINCHUIOT_USER_CHART_SETTINGS);
					}

				}).start();

			}

		}, SPLASH_DISPLAY_LENGTH);

		as.startNow();

	}

	private void showProgressDialog() {
		progressDialog = ProgressDialog.show(SplashActivity.this, "", getString(R.string.common_please_wait), true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
			}
		});
	}

	private void showLoginActivity() {
		Intent i = new Intent(Constants.Action.HSINCHUIOT_LOGIN);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
		progressDialog.dismiss();
		finish();

	}

	protected void gotoNormalUserMainScreen() {
		Intent intent = new Intent(Constants.Action.HSINCHUIOT_USER_MAIN);
		startActivity(intent);
		progressDialog.dismiss();
		finish();
	}

	protected void gotoAdminUserMainScreen() {
		Intent intent = new Intent(Constants.Action.HSINCHUIOT_SUPERUSER_MAIN);
		startActivity(intent);
		progressDialog.dismiss();
		finish();
	}
	
	private void showDebugActivity(String action){
		Intent intent = new Intent(action);
		startActivity(intent);
		progressDialog.dismiss();
		finish();
	}

	private void prepare() {
		//prepareThumbnailImage();
		ServiceContainer.getInstance().getUpgradeController().resetFirstTimeCheckFlag();
	}

	private void prepareThumbnailImage() {
		String imageDir = Constants.ImageLoader.IMAGE_ENGINE_CACHE;
		String siteImageFilename = "site_59.png";
		String thumbnailDir = imageDir + "/thumbnail";

		File dir = new File(thumbnailDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		Bitmap siteImage = ImageUtil.getBitmapFromResource(this.getResources(), R.drawable.site_a);
		ImageUtil.writeBitmapToFile(siteImage, imageDir + "/" + siteImageFilename);

		Bitmap thumbnailImage = ImageUtil.getImageThumbnail(imageDir + "/" + siteImageFilename, 128, 128);
		ImageUtil.writeBitmapToFile(thumbnailImage, thumbnailDir + "/" + siteImageFilename);

	}

	private void login() {
		String loginName = ServiceContainer.getInstance().getPerferenceService().getValue(
				Constants.PreferenceKey.LOGINNAME);
		String password = ServiceContainer.getInstance().getPerferenceService().getValue(
				Constants.PreferenceKey.PASSWORD);

		if (loginName.equals("") || password.equals("")) {
			showLoginActivity();
		} else {
			try {
				if (LoginService.getInstance().login(loginName, password)) {
					ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_WARNING,
							LoginService.getWarningThreshold(this));
					ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.THRESHOLD_BREACH,
							LoginService.getBreachThreshold(this));
					User loginUser = ServiceContainer.getInstance().getSessionService().getLoginUser();

					if (loginUser == null) {
						showLoginActivity();
					} else {
						if (loginUser.isAdminUser()) {
							gotoAdminUserMainScreen();
						} else if(loginUser.isNormalUser()) {
							int refreshTime = 10;
							String refreshTimeStr = ServiceContainer.getInstance().getPerferenceService().getValue(Constants.PreferenceKey.REALTIME_DATA_MONITOR_REFRESH_TIME);
							if(!"".equals(refreshTimeStr)){
								refreshTime = Integer.parseInt(refreshTimeStr);
							}
							
							ServiceContainer.getInstance().getSessionService().setSessionValue(Constants.SessionKey.REALTIME_DATA_MONITOR_REFRESH_TIME, refreshTime);
							gotoNormalUserMainScreen();
						}else{
							showLoginActivity();
						}
					}
				} else {
					showLoginActivity();
				}
			} catch (IOTException e) {
				showLoginActivity();
			}

		}
	}
}
