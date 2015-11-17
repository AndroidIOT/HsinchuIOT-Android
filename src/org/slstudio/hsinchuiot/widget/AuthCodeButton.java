package org.slstudio.hsinchuiot.widget;

import org.slstudio.hsinchuiot.Constants;
import org.slstudio.hsinchuiot.R;
import org.slstudio.hsinchuiot.service.ServiceContainer;
import org.slstudio.hsinchuiot.service.http.HttpConfig;
import org.slstudio.hsinchuiot.service.http.HttpRequest;
import org.slstudio.hsinchuiot.service.http.NoneAuthedHttpRequest;
import org.slstudio.hsinchuiot.service.http.RequestListener.DefaultRequestListener;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.Button;

public class AuthCodeButton extends Button {

	private AuthCodeListener listener;
	private TimeCount time;

	public AuthCodeListener getListener() {
		return listener;
	}

	public void setListener(AuthCodeListener listener) {
		this.listener = listener;
	}

	public AuthCodeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		time = new TimeCount(this, 60000, 1000);
	}

	public AuthCodeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		time = new TimeCount(this, 60000, 1000);
	}

	public AuthCodeButton(Context context) {
		super(context);
		time = new TimeCount(this, 60000, 1000);
	}

	public void cancel() {
		time.cancel();
	}

	public void retrieveAuthCode(String phone) {
		time.start();
		HttpRequest request = new RetrieveAuthCodeRequest(phone);
		ServiceContainer.getInstance().getHttpHandler()
				.doRequest(request, new DefaultRequestListener<Object>());

	}

	public interface AuthCodeListener {

	}

	class TimeCount extends CountDownTimer {
		Button button;

		public TimeCount(Button button, long millisInFuture,
				long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
			this.button = button;
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			button.setText(R.string.common_send_auth_code);
			button.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			button.setClickable(false);
			button.setText(millisUntilFinished / 1000 + "秒");
		}
	}

	public class RetrieveAuthCodeRequest extends NoneAuthedHttpRequest {

		public RetrieveAuthCodeRequest(String phone) {
			super(new HttpConfig.PostHttpConfig(),
					Constants.ServerAPIURI.COMMON_GETVERTIFYCODE);
			addParameter("phone", phone);

		}

	}
}
