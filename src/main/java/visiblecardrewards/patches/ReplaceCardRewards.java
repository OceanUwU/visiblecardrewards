package VisibleCardRewards.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.screens.CombatRewardScreen;
import VisibleCardRewards.rewards.SingleCardReward;

import java.util.ArrayList;

import static VisibleCardRewards.patches.NewRewardtypePatch.VCR_SINGLECARDREWARD;

@SpirePatch(clz=CombatRewardScreen.class, method="setupItemReward")
public class ReplaceCardRewards {
    public static void Postfix(CombatRewardScreen __instance) {
        ArrayList<RewardItem> rewardsToAdd = new ArrayList<RewardItem>();
        ArrayList<RewardItem> rewardsToRemove = new ArrayList<RewardItem>();
        for (RewardItem reward : __instance.rewards) {
            if (reward.type == RewardItem.RewardType.CARD) {
                ArrayList<SingleCardReward> cardOptions = new ArrayList<SingleCardReward>();
                for (AbstractCard c : reward.cards) {
                    int prevBlizz = AbstractDungeon.cardBlizzRandomizer;
                    cardOptions.add(new SingleCardReward(c));
                    AbstractDungeon.cardBlizzRandomizer = prevBlizz;
                }
                if (AbstractDungeon.player.hasRelic("Singing Bowl")) {
                    int prevBlizz = AbstractDungeon.cardBlizzRandomizer;
                    cardOptions.add(new SingleCardReward());
                    AbstractDungeon.cardBlizzRandomizer = prevBlizz;
                }
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