package com.binaryelysium.NPCMerchant.util;

import org.bukkit.Location;

public class Util {

    public static double distance( Location from, Location to, boolean height ) {
        if ( from.getWorld() != to.getWorld() )
            return 0;

        double x1 = from.getX();
        double x2 = to.getX();

        double z1 = from.getZ();
        double z2 = to.getZ();

        double y1 = from.getY();
        double y2 = to.getY();

        double dist = 0;
        if ( height ) {
            dist = Math.sqrt( Math.pow( ( x1 - x2 ), 2 ) + Math.pow( y1 - y2, 2 )
                    + Math.pow( z1 - z2, 2 ) );
        } else {
            dist = Math.sqrt( Math.pow( ( x1 - x2 ), 2 ) + Math.pow( z1 - z2, 2 ) );
        }
        return dist;
    }
}
