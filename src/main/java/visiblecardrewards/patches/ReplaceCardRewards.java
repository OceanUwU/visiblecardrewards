package visiblecardrewards.patches;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import pansTrinkets.patches.TrinketRewardTypePatch;
import pansTrinkets.rewards.TrinketReward;
import visiblecardrewards.rewards.SingleCardReward;

import java.util.ArrayList;
import java.util.Arrays;

import static visiblecardrewards.patches.NewRewardtypePatch.VCR_SINGLECARDREWARD;

@SpirePatch(clz=CombatRewardScreen.class, method="setupItemReward")
public class ReplaceCardRewards {
    public static ArrayList<String> rewardsToReplace = new ArrayList<String>(Arrays.asList("CARD", "DAZINGPULSE", "DECABEAM", "DONUBEAM", "EXPLODE", "SPIKE", "BOSSCARD", "JAXCARD", "UPGRADEDUNKNOWNCARD", "SEALCARD", "GEM", "GEMALLRARITIES", "PANS_TRINKET_TRINKET_REWARD"));

    public static void Postfix(CombatRewardScreen __instance) {
        ArrayList<RewardItem> rewardsToAdd = new ArrayList<RewardItem>();
        ArrayList<RewardItem> rewardsToRemove = new ArrayList<RewardItem>();
        for (RewardItem reward : __instance.rewards) {
            if (rewardsToReplace.contains(reward.type.name())) {
                ArrayList<SingleCardReward> cardOptions = new ArrayList<SingleCardReward>();

                if (Loader.isModLoaded("PansTrinkets") && reward.type == TrinketRewardTypePatch.PANS_TRINKET_TRINKET_REWARD) {
                    for (AbstractCard c : ((TrinketReward)reward).linkedReward.cards)
                        cardOptions.add(new SingleCardReward(c));
                    for (AbstractCard c : reward.cards) {
                        SingleCardReward scr = new SingleCardReward(c);
                        scr.isTrinket = true;
                        cardOptions.add(scr);
                    }
                    rewardsToRemove.add(((TrinketReward)reward).linkedReward);
                } else {
                    for (AbstractCard c : reward.cards)
                        cardOptions.add(new SingleCardReward(c));
                }

                if (AbstractDungeon.player.hasRelic("Singing Bowl"))
                    cardOptions.add(new SingleCardReward());
                
                for (SingleCardReward option : cardOptions) {
                    if (AbstractDungeon.player.hasRelic("YesRelic") && option.type == VCR_SINGLECARDREWARD)
                        continue;
                    for (SingleCardReward otherOption : cardOptions) {
                        if (option != otherOption)
                            option.addCardLink(otherOption);
                    }
                }

                rewardsToAdd.addAll(cardOptions);
                rewardsToRemove.add(reward);
            }
        }
        __instance.rewards.addAll(rewardsToAdd);
        __instance.rewards.removeAll(rewardsToRemove);
        __instance.positionRewards();
    }
}