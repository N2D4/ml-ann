package com.n2d4.rachel.main.gameengines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.n2d4.rachel.util.Requirements;

public class CardGame {

	public CardGame() {
		// Quite dusty here...
	}
	
	
	
	
	
	
	
	public static double getSimpleValue(Card[] hand, int l, boolean col) {
		Card[] cards = Card.sort(hand, col);
		
		int prev = -1;
		int tot = 0;
		for (Card card : cards) {
			int id = col ? card.getColor().ordinal() : card.getType().ordinal();
			if (id == prev) {
				tot++;
				if (tot == l) return 1;
			} else {
				tot = 1;
				prev = id;
			}
		}
		return 0;
	}
	
	
	
	
	
	public static double getThrowValue(Card... hand) {
		if (hand.length == 0) return 1;
		CardColor col = hand[0].getColor();
		for (int i = 1; i < hand.length; i++) {
			if (hand[i].getColor() != col) {
				return 0;
			}
		}
		return 1;
	}
	
	
	
	
	
	public static double getPokerValue(Card... hand) {
		Card[] cards = Card.sort(hand);
		CardType[] cardTypes = new CardType[cards.length];
		for (int i = 0; i < cards.length; i++)
			cardTypes[i] = cards[i].getType();
		double result = getCardRanks(cardTypes);
	
		
		
		//
		// Check for a Straight
		//
		outer: for (int i = 0; i < cards.length - 5; i++) {
			Card highest = cards[i];
			boolean isFlush = true;
			for (int j = 1; j < 5; j++) {
				i++;
				if (cards[i].getValue() != highest.getValue() - j) {
					continue outer;
				}
				if (cards[i].getColor() != highest.getColor()) isFlush = false;
			}
			
			CardType[] em = new CardType[5];
			for (int j = 0; j < 5; j++)
				em[j] = CardType.values()[highest.getValue() - j];
			double nres = 4 + getCardRanks(em, cardTypes);
			if (isFlush) {	// Straight Flush?
				nres += 4;
				if (highest.getType() == CardType.ACE) {	// Royal Flush?!?
					nres += 1;
				}
			}
			result = Math.max(result, nres);
		}
		
		
		
		
		//
		// Check for a Flush
		//
		int[] each = new int[CardColor.values().length];
		for (int i = 0; i < cards.length; i++) {
			each[cards[i].getColor().ordinal()]++;
		}
		
		for (int i = 0; i < each.length; i++) {
			if (each[i] >= 5) {
				CardColor col = CardColor.values()[i];
				List<CardType> types = new ArrayList<CardType>();
				for (int j = 0; j < cards.length; j++) {
					if (cards[j].getColor() == col) types.add(cards[j].getType());
				}
				double nres = 5 + getCardRanks(types.toArray(new CardType[0]), cardTypes);
				result = Math.max(result, nres);
			}
		}
		
		
		
		
		//
		// Check for Pairs, Three and Four Of A Kind
		//

		int[] eachType = new int[CardType.values().length];
		for (int i = 0; i < cards.length; i++) {
			eachType[cards[i].getType().ordinal()]++;
		}
		
		CardType pairs1 = null;
		CardType pairs2 = null;
		CardType threes = null;
		CardType fours = null;
		for (int i = eachType.length - 1; i >= 0; i--) {
			if (eachType[i] >= 4 && fours == null) {
				fours = CardType.values()[i];
			} else if (eachType[i] == 3 && threes == null) {
				threes = CardType.values()[i];
			} else if (eachType[i] == 2) {
				if (pairs1 == null) {
					pairs1 = CardType.values()[i];
				} else if (pairs2 == null) {
					pairs2 = CardType.values()[i];
				}
			}
		}
		
		if (fours != null) {											// Four Of A Kind
			double nres = 7 + getCardRanks(fours, cardTypes);
			result = Math.max(result, nres);
			
		} else if (threes != null && pairs1 != null) {					// Full House
			double nres = 6 + getCardRanks(new CardType[] {threes, pairs1}, cardTypes);
			result = Math.max(result, nres);
			
		} else if (threes != null) {									// Three Of A Kind
			double nres = 3 + getCardRanks(threes, cardTypes);
			result = Math.max(result, nres);
			
		} else if (pairs2 != null) {									// Two Pair
			double nres = 2 + getCardRanks(new CardType[] {pairs1, pairs2}, cardTypes);
			result = Math.max(result, nres);
			
		} else if (pairs1 != null) {									// Pair
			double nres = 1 + getCardRanks(pairs1, cardTypes);
			result = Math.max(result, nres);
		}
		
		
		
		return result;
	}
	
	
	private static double getCardRanks(CardType[] cards) {
		return getCardRanks(new CardType[] {}, cards);
	}
	
	private static double getCardRanks(CardType card, CardType[] cards) {
		return getCardRanks(new CardType[] {card}, cards);
	}
	
	private static double getCardRanks(CardType[] cards1, CardType[] cards2) {
		CardType[] sorted1 = CardType.sort(cards1);
		CardType[] sorted2 = CardType.sort(cards2);
		CardType[] c = Arrays.copyOf(sorted1, 5);
		int j = 0;
		outer: for (int i = sorted1.length; i < c.length; j++) {
			for (int k = 0; k < sorted1.length; k++) {
				if (sorted2[j] == sorted1[k]) {
					sorted1[k] = null;
					continue outer;
				}
			}
			c[i++] = sorted2[j];
		}
		return pgetCardRanks(c);
	}
	
	private static double pgetCardRanks(CardType... cards) {
		double result = 0;
		
		double kstep = 1d / (CardType.getTotalCount() + 1);
		double curstep = 1;
		for (CardType card : cards) {
			result += (curstep *= kstep) * card.getValue();
		}
		
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	public static class Card implements Comparable<Card> {

		private static final Card[][] all = new Card[CardType.values().length][CardColor.values().length];
		private static final Card[] stack = initializeStack();
		
		private final CardType type;
		private final CardColor color;

		
		
		private Card(CardType type, CardColor color) {
			this.type = type;
			this.color = color;
		}
		
		
		public static Card getCard(CardType type, CardColor color) {
			Card result = all[type.ordinal()][color.ordinal()];
			if (result == null) {
				all[type.ordinal()][color.ordinal()] = result = new Card(type, color);
			}
			return result;
		}
		
		
		private static Card[] initializeStack() {
			CardType[] types = CardType.values();
			CardColor[] colors = CardColor.values();
			
			Card[] result = new Card[types.length * colors.length];
			
			for (int i = 0; i < types.length; i++) {
				for (int j = 0; j < colors.length; j++) {
					result[i * colors.length + j] = getCard(types[i], colors[j]);
				}
			}
			
			return result;
		}
		
		public String getName() {
			return getColor().getName() + getType().getName();
		}
		
		public int getPoints() {
			return type.getPoints();
		}
		
		public final int getRank() {
			return type.getRank();
		}
		
		public final int getValue() {
			return type.getValue();
		}
		
		public CardType getType() {
			return type;
		}
		
		public CardColor getColor() {
			return color;
		}
		
		
		
		public static final Card[] getShuffledStack() {
			List<Card> result = new ArrayList<Card>(Arrays.asList(stack));
			Collections.shuffle(result);
			return result.toArray(stack);
		}
		
		public static final Card getRandom() {
			return new Card(CardType.getRandom(), CardColor.getRandom());
		}
		
		public static final Card[] getRandom(int count) {
			Requirements.nonNegative(count, "card count");
			Requirements.smallerThan(count, stack.length, "card count");
			
			return Arrays.copyOf(getShuffledStack(), count);
		}
		
		
		
		
		public static final Card[] sort(Card[] cards) {
			Card[] copy = Arrays.copyOf(cards, cards.length);
			Arrays.sort(copy, Collections.reverseOrder());
			
			return copy;
		}
		
		public static final Card[] sort(Card[] cards, boolean byColor) {
			if (!byColor) return sort(cards);
			Card[] copy = Arrays.copyOf(cards, cards.length);
			Arrays.sort(copy, (a1, a2) -> a1.getColor().compareTo(a2.getColor()));
			
			return copy;
		}


		@Override
		public int compareTo(Card o) {
			return getType().compareTo(o.getType());
		}
		
		@Override
		public String toString() {
			return getColor().toString() + " " + getType().toString();
		}
		
	}
	
	
	
	
	
	
	
	
	
	public static enum CardColor {
		HEARTS("H"),
		SPADES("S"),
		DIAMONDS("D"),
		CLUBS("C");
		
	
		private final String name;
		
		private CardColor(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public static final CardColor getRandom() {
			CardColor[] values = CardColor.values();
			return values[(int) (Math.random() * values.length)];
		}
	}
	
	public static enum CardType implements Comparable<CardType> {

		TWO("2", 0),
		THREE("3", 0),
		FOUR("4", 0),
		FIVE("5", 0),
		SIX("6", 0),
		SEVEN("7", 0),
		EIGHT("8", 0),
		NINE("9", 0),
		TEN("10", 10),
		
		JACK("J", 2),
		QUEEN("Q", 3),
		KING("K", 4),
		ACE("A", 11);
		

		
		
		
		
		private final String name;
		private final int points;
		
		private CardType(String name, int points) {
			this.name = name;
			this.points = points;
		}
		
		public String getName() {
			return name;
		}
		
		public int getPoints() {
			return points;
		}
		
		public final int getRank() {
			return values().length - getValue();
		}
		
		public final int getValue() {
			return this.ordinal();
		}
		
		public static final CardType getRandom() {
			CardType[] values = values();
			return values[(int) (Math.random() * values.length)];
		}
		
		public static final CardType[] sort(CardType[] cards) {
			CardType[] copy = Arrays.copyOf(cards, cards.length);
			Arrays.sort(copy);
			return copy;
		}
		
		
		public static final int getTotalCount() {
			return values().length;
		}
		
		
	}

}
