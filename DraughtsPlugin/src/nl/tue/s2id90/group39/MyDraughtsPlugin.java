package nl.tue.s2id90.group39;

import nl.tue.s2id90.samples.RotterDammerJr;
import nl.tue.s2id90.samples.UninformedPlayer;
import nl.tue.s2id90.samples.OptimisticPlayer;
import nl.tue.s2id90.samples.BuggyPlayer;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.tue.s2id90.draughts.DraughtsPlayerProvider;
import nl.tue.s2id90.draughts.DraughtsPlugin;
import nl.tue.s2id90.samples.AmsterDammerNoIterativeDeepening;
import nl.tue.s2id90.samples.RotterDammerJr_backup2;
import nl.tue.s2id90.samples.RotterDammer_backup1;
import nl.tue.s2id90.samples.RotterDammer_backup3;
import nl.tue.s2id90.samples.RotterDammer_checkRow_bugged;



/**
 *
 * @author huub
 */
@PluginImplementation
public class MyDraughtsPlugin extends DraughtsPlayerProvider implements DraughtsPlugin {
    public MyDraughtsPlugin() {
        // make one or more players available to the AICompetition tool
        // During the final competition you should make only your 
        // best player available. For testing it might be handy
        // to make more than one player available.
        super(new RotterDammer(5),
                new RotterDammer(5),
                new AmsterDammerNoIterativeDeepening(5),
                new RotterDammerJr(5),
                new RotterDammerJr_backup2(5),
                new RotterDammer_backup1(5),
                new RotterDammer_backup3(5),
                new RotterDammer_checkRow_bugged(5)
        );
    }
}
