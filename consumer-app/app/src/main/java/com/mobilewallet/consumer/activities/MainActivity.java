package com.mobilewallet.consumer.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobilewallet.consumer.R;
import com.mobilewallet.consumer.fragments.CardsFragment;
import com.mobilewallet.consumer.fragments.HomeFragment;
import com.mobilewallet.consumer.fragments.NotificationsFragment;
import com.mobilewallet.consumer.fragments.ProfileFragment;
import com.mobilewallet.consumer.fragments.TransactionsFragment;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private final HomeFragment homeFragment = new HomeFragment();
    private final TransactionsFragment transactionsFragment = new TransactionsFragment();
    private final CardsFragment cardsFragment = new CardsFragment();
    private final NotificationsFragment notificationsFragment = new NotificationsFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();

    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupFragments();
        setupBottomNavigation();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
    }

    private void setupFragments() {
        fm.beginTransaction().add(R.id.fragmentContainer, profileFragment, "5").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.fragmentContainer, notificationsFragment, "4").hide(notificationsFragment).commit();
        fm.beginTransaction().add(R.id.fragmentContainer, cardsFragment, "3").hide(cardsFragment).commit();
        fm.beginTransaction().add(R.id.fragmentContainer, transactionsFragment, "2").hide(transactionsFragment).commit();
        fm.beginTransaction().add(R.id.fragmentContainer, homeFragment, "1").commit();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fm.beginTransaction().hide(active).show(homeFragment).commit();
                active = homeFragment;
                return true;
            } else if (itemId == R.id.nav_transactions) {
                fm.beginTransaction().hide(active).show(transactionsFragment).commit();
                active = transactionsFragment;
                return true;
            } else if (itemId == R.id.nav_cards) {
                fm.beginTransaction().hide(active).show(cardsFragment).commit();
                active = cardsFragment;
                return true;
            } else if (itemId == R.id.nav_notifications) {
                fm.beginTransaction().hide(active).show(notificationsFragment).commit();
                active = notificationsFragment;
                return true;
            } else if (itemId == R.id.nav_profile) {
                fm.beginTransaction().hide(active).show(profileFragment).commit();
                active = profileFragment;
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
}