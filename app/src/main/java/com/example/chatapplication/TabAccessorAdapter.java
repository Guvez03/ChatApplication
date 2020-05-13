package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessorAdapter extends FragmentPagerAdapter
{
    public TabAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {

        switch (i)    // hangi fragmenta tıklanırsa oraya gider
        {
            case 0 :
                ChatFragment chatFragment = new ChatFragment();
                return chatFragment;
            case 1 :
                GroupsFragment groupsFragment = new GroupsFragment();
                return groupsFragment;
            case 2 :
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
            case 3 :
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
        }

        return null;
    }

    @Override
    public int getCount() {   // kaç bölüm olduğunu gösterdik
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {  // bölümlerin isimlerini girdik böyle gözükecekler
        switch (position)
        {
            case 0 :
                return "Sohbet";
            case 1 :
                return "Grup";
            case 2 :
                return "Etkileşim";
            case 3 :
                return "İstekler";

        }

        return null;
    }
}
