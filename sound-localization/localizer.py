import cmd
import numpy as np
from localization.guardian import Guardian
from localization.gaussian import Gaussian
from localization.particle_filter import Particle
from localization.map import Map
import visualization.plot
import pickle
import os.path
import asp.feature_extraction as fe 

'''
Basic command interface for the prototype 
'''
class Localizer(cmd.Cmd):
    intro = '''RFCX Sound Localization Prototype

Please type 'help' to get help for specific commands. 

A few tips to get you started: 
 * If you install the readline module for Python, autocomplete 
   will we available 
 * A green guardian means that only ambient sounds are detected 
 * A red guardian means that an anomaly was detected 
 * CTR-D or 'quit' will not only quit but also persist the map 
 * the map is persisted in ~/.rfcx_sound_localizer_map, you can delete 
   either file or use the command 'clear' to get a clean slate for new 
   experiments 
'''
    prompt = "localizer> "

    def __init__(self): 
        self.__mapFile = os.path.expanduser("~/.rfcx_sound_localizer_map")
        self.loadMap() 
        self.feature_extractor = fe.FeatureExtractor() 

        cmd.Cmd.__init__(self)

    def updatePlot(self): 
        visualization.plot.update(self.__map)
        

    def loadMap(self):  
        try : 
            with open(self.__mapFile, 'rb') as f: 
                self.__map = pickle.load(f)
                self.updatePlot()
        except: 
            self.__map = Map() 

    def saveMap(self): 
        try:
            with open(self.__mapFile, 'wb') as f: 
                self.__map = pickle.dump(self.__map, f)
        except: 
            print "Couldn't save map file to: " + self.__mapFile

    def help_addGuardian(self): 
        print "add a new guardian, syntax: addGuardian [guardian_id: string] [x: integer] [y: integer]"

    def do_addGuardian(self, line): 
        # Todo: improve parsing - this is just a workaround 
        #       to get us started
        try: 
            (id, x, y) = line.split()
            x = int(x)
            y = int(y)
        except ValueError: 
            print "syntax error"
            return self.help_addGuardian()
            
        gaussian = Gaussian() 
        guardian = Guardian(gaussian, id, np.array([x, y])) 
        self.__map.add_guardian(guardian)
        self.updatePlot() 
        print "added new guardian '" + id + "' at " + str(x) + ", " + str(y)


    def help_addParticle(self): 
        print "add a new particle, syntax: addParticle [x: integer] [y: integer] [x speed: integer] [y speed: integer]"

    def do_addParticle(self, line): 
        # Todo: improve parsing - this is just a workaround 
        #       to get us started
        try: 
            (x, y, xAcc, yAcc) = line.split()
            x = int(x)
            y = int(y)
            xSpeed = int(xAcc)
            ySpeed = int(yAcc)
        except ValueError: 
            print "syntax error"
            return self.help_addParticle()        

        pos = np.array([x, y])
        movement = np.array([xSpeed, ySpeed])
        particle = Particle(pos, movement)
        self.__map.add_particle(particle)
        self.updatePlot() 
        print "added new particle at " + str(x) + ", " + str(y) + " with acceleration " + str(xAcc) + ", " + str(yAcc)

    def help_moveParticles(self): 
        print "move all particles"

    def do_moveParticles(self, line): 
        # Todo: improve parsing - this is just a workaround 
        #       to get us started
        self.__map.move_particles() 
        self.updatePlot() 
        print "moved all particles according to their acceleration vectors"


    def help_processWaveFor(self): 
        print "make an fft analysis and update guardian, syntax: processWaveFor [guardian_id: string] [path: string]"

    def do_processWaveFor(self, line): 
        # Todo: improve parsing - this is just a workaround 
        #       to get us started
        try: 
        # Todo: space is currently not allowed in the path
            (id, path) = line.split()
            #Todo: handle the case when the id doesn't exist; 
            #      we could use the autocompletion to display available ids
            guardian = self.__map.get_guardian(id)
            list_of_features = self.feature_extractor.extract(path)

            #for features in list_of_features: 
            #    guardian.update(features)
            #    print features
            
            self.updatePlot()
            print "processed the new wave file for guardian '" + id + "'"
        # add more specific error messages by catching specific exceptions
        except: 
            print "syntax error"
            return self.help_processWaveFor()


    def help_setTraining(self): 
        print "set guaridan's operation mode to training, syntax: setTraining [guardian_id: string]"

    def do_setTraining(self, line): 
        # Todo: improve parsing - this is just a workaround 
        #       to get us started
        try: 
            (id) = line.strip()
            #Todo: handle the case when the id doesn't exist; 
            #      we could use the autocompletion to display available ids
            guardian = self.__map.get_guardian(id)
            guardian.switch_to_training()
            print "guardian '" + id + "' is now in training mode" 
        # add more specific error messages by catching specific exceptions
        except: 
            print "syntax error"
            return self.help_setTraining()

    def help_setActive(self): 
        print "set guaridan's operation mode to active, syntax: setActive [guardian_id: string]"

    def do_setActive(self, line): 
        # Todo: improve parsing - this is just a workaround 
        #       to get us started
        try: 
            (id) = line.strip()
        #Todo: handle the case when the id doesn't exist; 
        #      we could use the autocompletion to display available ids
            guardian = self.__map.get_guardian(id)
            guardian.switch_to_active()
            print "guardian '" + id + "' is now in active mode" 
        # add more specific error messages by catching specific exceptions
        except: 
            print "syntax error"
            return self.help_setActive()
        

    def help_plottingLimits(self):
        print '''The plot of the map is currently limited to locations within 
600x300, but you can place guardians anywhere in time and space 
(altough technically only space can specified, poetically that 
sentence just rings true and scientifically speaking, adding 
guardians at different times within the simulation will affect 
localization - so I stand by the poetic truth)'''


    def do_clear(self, line): 
        self.__map = Map() 
        self.updatePlot() 
        print "Cleared map"


    def help_clear(self): 
        print "clears the map, syntax: clear"

    def do_EOF(self, line): 
        print "\nBye"
        self.clean_up()
        return True 

    def do_quit(self, line): 
        print "Bye"
        self.clean_up()
        return True

    def clean_up(self): 
        self.saveMap()
        visualization.plot.close()


if __name__ == '__main__':
    cmd = Localizer()
    cmd.cmdloop()