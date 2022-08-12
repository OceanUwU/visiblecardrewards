package visiblecardrewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.SingingBowl;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import visiblecardrewards.rewards.SingleCardReward;

import static visiblecardrewards.patches.NewRewardtypePatch.VCR_SINGLECARDREWARD;
import static visiblecardrewards.patches.NewRewardtypePatch.VCR_BOWLREWARD;

public class BowlPickup {
    private static boolean bowlPickedUp = false;

    @SpirePatch(clz=AbstractRelic.class, method="onEquip")
    public static class BowlTrack {
        public static void Postfix(AbstractRelic __instance) {
            if (__instance instanceof SingingBowl)
                bowlPickedUp = true;
        }
    }

    @SpirePatch(clz=CombatRewardScreen.class, method="rewardViewUpdate")
    public static class AddReward {
        public static void Postfix(CombatRewardScreen __instance) {
            if (bowlPickedUp) {
                int index = 0;
                for (RewardItem reward : AbstractDungeon.combatRewardScreen.rewards) {
                    index++;
                    if (reward instanceof SingleCardReward && reward.type == VCR_SINGLECARDREWARD) {
                        boolean hasHpReward = false;
                        for (SingleCardReward link : ((SingleCardReward)reward).cardLinks) {
                            if (link.type == VCR_BOWLREWARD) {
                                hasHpReward = true;
                                break;
                            }
                        }
                        if (hasHpReward) continue;
                        if (!((SingleCardReward)reward).isLast()) continue;
                        SingleCardReward hpReward = new SingleCardReward();
                        hpReward.addCardLink((SingleCardReward)reward);
                        for (SingleCardReward link : ((SingleCardReward)reward).cardLinks) {
                            hpReward.addCardLink(link);
                        }
                        AbstractDungeon.combatRewardScreen.rewards.add(index, hpReward);
                        AbstractDungeon.combatRewardScreen.positionRewards();
                        Postfix(__instance);
                        bowlPickedUp = false;
                        return;
                    }
                }
                bowlPickedUp = false;
            }
        }
    }
}