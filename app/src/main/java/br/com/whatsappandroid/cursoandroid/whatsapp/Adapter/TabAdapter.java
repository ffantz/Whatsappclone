package br.com.whatsappandroid.cursoandroid.whatsapp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import br.com.whatsappandroid.cursoandroid.whatsapp.fragment.ContatosFragment;
import br.com.whatsappandroid.cursoandroid.whatsapp.fragment.ConversasFragment;

public class TabAdapter extends FragmentStatePagerAdapter{
    private String[] titulosAbas = {"Conversas", "Contatos"};

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new ConversasFragment();
                break;
            case 1:
                fragment = new ContatosFragment();
                break;

        }

        return fragment;
    }

    @Override
    public int getCount() {
        return titulosAbas.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titulosAbas[position];
    }
}

