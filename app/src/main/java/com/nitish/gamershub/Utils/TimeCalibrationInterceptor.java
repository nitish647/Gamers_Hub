package com.nitish.gamershub.Utils;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.HttpDate;

import java.io.IOException;
import java.util.Date;

public class TimeCalibrationInterceptor implements Interceptor {
    long minResponseTime = Long.MAX_VALUE;

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = SystemClock.elapsedRealtime();
        Response response = chain.proceed(request);
        long responseTime = (SystemClock.elapsedRealtime() - startTime) / 2;

        Headers headers = response.headers();
        calibration(responseTime, headers);
        return response;
    }

    private void calibration(long responseTime, Headers headers) {
        if (headers == null) {
            return;
        }

        // If the current response time is less than the previous min one, calibrate again
        if (responseTime >= minResponseTime) {
            return;
        }

        String standardTime = headers.get("Date");
        if (!TextUtils.isEmpty(standardTime)) {
            Date parse = HttpDate.parse(standardTime);
            if (parse != null) {
                TimeManager.getInstance().initServerTime(parse.getTime() + responseTime);

                Date date = new Date(TimeManager.getInstance().getServerTime());

                Log.d("currentNtpTIme : ",DateTimeHelper.convertDateToString(date));

                minResponseTime = responseTime;
            }
        }
    }
}