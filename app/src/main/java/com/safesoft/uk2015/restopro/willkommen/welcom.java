package com.safesoft.uk2015.restopro.willkommen;

import android.content.Intent;

import com.safesoft.uk2015.restopro.Activities.LoginActivity;
import com.formation.uk2015.login.R;
import com.stephentuso.welcome.WelcomeScreenBuilder;
import com.stephentuso.welcome.ui.WelcomeActivity;
import com.stephentuso.welcome.util.WelcomeScreenConfiguration;

/**
 * Created by UK2015 on 18/06/2016.
 */
public class welcom extends WelcomeActivity {

    @Override
    protected WelcomeScreenConfiguration configuration() {
        return new WelcomeScreenBuilder(this)
                .animateButtons(true)
                .theme(R.style.CustomWelcomeScreenTheme)
                .defaultTitleTypefacePath("Montserrat-Bold.ttf")
                .defaultHeaderTypefacePath("Montserrat-Bold.ttf")
                .titlePage(R.mipmap.welcome, "Welcome To RESTO PRO Application", R.color.colorPrimary)
                .basicPage(R.mipmap.c002, "Simple to use", "Save your time and raise your productivity with RESTOPRO application.", R.color.colorPrimary)
                .parallaxPage(R.layout.parallax_example, "Easy to bring along", "Use it with many other applications and communicate with them in the real time", R.color.colorPrimary, 0.2f, 2f)
                .basicPage(R.mipmap.training_human, "Successful", "Organize your job and get satisfaction of your client.", R.color.colorPrimary)
                .swipeToDismiss(true)
                .exitAnimation(android.R.anim.fade_out)
                .build();
    }

    @Override
    protected void completeWelcomeScreen() {
        Intent postviewIntent = new Intent(this, LoginActivity.class);
        // postviewIntent.putExtras(bundleinfo);
        startActivityForResult(postviewIntent, 600);
        this.overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
    }

    public static String welcomeKey() {
        return "WelcomeScreen";
    }

}

