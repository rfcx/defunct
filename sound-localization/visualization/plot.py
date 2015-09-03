import sys, pygame

'''
Basic visualization
Todo: use animation from matplotlib or replace by pygame  
'''

pygame.init()
size = width, height = 1000, 562

white = 255, 255, 255
red = 255, 0, 0 
green =  0, 255, 0 

screen = pygame.display.set_mode(size)
pygame.display.set_caption('rfcx Real Time Tracking of Vehicles')
screen.fill(white)

car = pygame.image.load("./resources/car.png")
car_rect = car.get_rect()


def update(map): 
    screen.fill(white)

    for guardian in map.guardians:
        pos = guardian.pos 
        x = int(pos[0])
        y = int(pos[1])

        color = green 

        if guardian.detection > 0.5: 
            color = red

        pygame.draw.circle(screen, color, (x,y), 20, 0)

    for particle in map.particles: 
        x = int(particle.pos[0])
        y = int(particle.pos[1])

        screen.blit(car, (x,y))

    pygame.display.flip()
    
def close(): 
    pygame.display.quit()