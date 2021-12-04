/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Moisture extends Buff {
	
	{
		type = buffType.POSITIVE;
	}

	private static final float STEP	= 10f;
	
	public static final float MAX = 100f;
	public static final float MOIST = 50f;
	public static final float DRY	= 30f;
	public static final float ARID	= 10f;
	
	
	private int interval = 1;
	private float level = 0;
	private float partialDamage;

	
	@Override
	public boolean act() {
		
		if (target.isAlive() && target instanceof Hero) {

			Hero hero = (Hero)target;
			
			if(hero.heroClass == HeroClass.GOO_HERO) {
				if (isArid()) {

					partialDamage += STEP * target.HT/1000f;

					if (partialDamage > 1){
						target.damage( (int)partialDamage, this);
						partialDamage -= (int)partialDamage;
					}
					GLog.w( Messages.get(this, "desc_intro_arid") );
					
				} else {

					float newLevel = level - STEP;
					if (newLevel <= ARID) {

						GLog.n( Messages.get(this, "onarid") );
						hero.resting = false;
						hero.damage( 1, this );

						hero.interrupt();

					} else if (newLevel <= DRY && level > DRY) {

						GLog.w( Messages.get(this, "ondry") );

					}
					level = newLevel;

				}
			}
		}
		spend( STEP );
		return true;
		
	}
		
	public boolean isArid() {
		return level <= ARID;
	}

	public int moisture() {
		return (int)Math.ceil(level);
	}
	
	public float level() {
		return level;
	}
	
	public void set( int value, int time ) {
		
		level = Math.min(MAX, value + level);
		if (level <= 0) level = 1;
		interval = time;

	}
	
	@Override
	public int icon() {
		if(level > MOIST) return BuffIndicator.MOIST;
		else if(level > DRY) return BuffIndicator.NONE;
		else if (level > ARID) return BuffIndicator.DRY;
		else return BuffIndicator.ARID;
	}


	@Override
	public String toString() {
		if (level > MOIST) {
			return Messages.get(this, "moist");
		} else if (level > DRY) {
			return Messages.get(this, "moist");
		} else if (level > ARID) {
			return Messages.get(this, "dry");
		} else {
			return Messages.get(this, "arid");
		}
	}


	@Override
	public String desc() {
		String result;
		if (level > MOIST) {
			result = Messages.get(this, "desc_intro_moist");
		} else if (level > DRY) {
			result = Messages.get(this, "desc_intro_dry");
		} else {
			result = Messages.get(this, "desc_intro_arid");
		}

		result += Messages.get(this, "desc");

		return result;
	}
	
	private static final String LEVEL	    = "level";
	private static final String INTERVAL    = "interval";
	private static final String PARTIALDAMAGE 	= "partialDamage";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( INTERVAL, interval );
		bundle.put( LEVEL, level );
		bundle.put( PARTIALDAMAGE, partialDamage );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		interval = bundle.getInt( INTERVAL );
		level = bundle.getFloat( LEVEL );
		partialDamage = bundle.getFloat(PARTIALDAMAGE);
	}
}
