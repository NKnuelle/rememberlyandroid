package de.rememberly.rememberlyandroidapp.remote;

import java.util.List;

import de.rememberly.rememberlyandroidapp.model.HttpResponse;

/**
 * Created by nilsk on 14.03.2019.
 */

public interface IApiCallback {

    /** This callback method is used in an implementing class (e.g an activity) to react to
     * a successful API call to the Rememberly Server.
     *
     * @param apiRequestCode The requestcode identifies which API method was called.
     * @param httpResponse The response from the API call.
     * */
    void onSuccess(int apiRequestCode, HttpResponse httpResponse);

    /** This callback method is used in an implementing class (e.g. an activity) to react to
     * an incorrect API call to the Rememberly Server.
     *
     * @param apiRequestCode The requestcode identifies which API method was called.
     * @param httpResponse The response from the API call.
     */
    void onError(int apiRequestCode, HttpResponse httpResponse);

    /** This callback method is used in an implementing class (e.g. an activity) to react to
     * a failed API call to the Rememberly Server.
     *
     * @param apiRequestCode The requestcode identifies which API method was called.
     * @param t The exception (throwable) of the failure.
     */
    void onFailure(int apiRequestCode, Throwable t);
}
