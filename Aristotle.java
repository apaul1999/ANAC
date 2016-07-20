package aristotle;

import negotiator.AgentID;
import negotiator.Bid;
import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.Offer;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.ValueDiscrete;
import negotiator.parties.AbstractNegotiationParty;
import negotiator.session.TimeLineInfo;
import negotiator.utility.AbstractUtilitySpace;
import negotiator.utility.UtilitySpace;

import java.util.ArrayList;
import java.util.List;

/**
 * This is your negotiation party.
 */
public class Aristotle extends AbstractNegotiationParty
{

    private double agreeVal = 0.73;
    private Bid lastBid;
    private ArrayList<Issue> issues;
    private ArrayList<Bid> bids;

    //int counter;
    @Override
    public void init(AbstractUtilitySpace utilSpace, Deadline dl, TimeLineInfo tl, long randomSeed, AgentID agentId)
    {
        super.init(utilSpace, dl, tl, randomSeed, agentId);
        int size = 50;
        bids = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            bids.add(generateRandomBid());

        bids.sort((a, b) -> new Double(getUtility(b)).compareTo(getUtility(a)));
    }

    @Override
    public AgentID getPartyId()
    {
        return new AgentID("Aristotle");
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> validActions)
    {
        try
        {
            if (lastBid != null && getUtilitySpace().getUtility(lastBid) > agreeVal)
            {
                return new Accept();
            } else if (timeline.getTime() >= 0.985)
            {
                return new Accept();
            } else
            {
                return new Offer(createBid());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void receiveMessage(AgentID sender, Action action)
    {
        super.receiveMessage(sender, action);
        if (!(action instanceof Accept))
        {
            Bid b = Action.getBidFromAction(action);
            lastBid = b;
        }
        if (sender != null)
        {
            //System.out.println(sender.toString());
            if (lastBid != null && !action.equals(new Accept()))
            {
                issues = lastBid.getIssues();
                try
                {
                    for (int i = 0; i < issues.size(); i++)
                    {
                        IssueDiscrete id = (IssueDiscrete) utilitySpace.getMaxUtilityBid().getIssues().get(i);
                        int choice = id.getValueIndex((ValueDiscrete) lastBid.getValue(i + 1));
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        //counter++;
    }

    public Bid createBid()
    {
        if (lastBid == null)
        {
            Bid b = null;
            try
            {
                b = new Bid(getUtilitySpace().getMaxUtilityBid());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return b;
        } else
        {
            Bid b;

            if (Math.random() < 0.05)
            {
                b = generateRandomBid();
            } else
            {
                b = bids.get((int) (timeline.getTime() * bids.size() * 3. / 4.));
            }

            try
            {
                return (b != null) ? b : utilitySpace.getMaxUtilityBid();
            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static ValueDiscrete getVal(UtilitySpace us, int issue, int valID)
    {
        try
        {
            IssueDiscrete is = (IssueDiscrete) ((AbstractUtilitySpace) us).getMaxUtilityBid().getIssues().get(issue);
            return is.getValue(valID);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
