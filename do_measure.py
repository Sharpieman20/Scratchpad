
'''

'''


def get_min_inc_from_sens(sens):
    return 0.15*(8.0*((sens*0.6+0.2)**3))

def get_degrees_per_pixel(height):
    assert height == 16384
    return 0.00220031449588

def get_precise_start_angle(measured_angle, min_inc):
    mult = measured_angle / min_inc
    precise_angle = round(mult) * min_inc
    return precise_angle

def get_displayed_angle(precise_angle):
    displayed_angle = precise_angle
    while displayed_angle < -180.0:
        displayed_angle += 360.0
    while displayed_angle > 180.0:
        displayed_angle -= 360.0
    return displayed_angle

# get from options.txt. crucial that this is the exact correct value.
my_sens = 0.0054852544078054346
# DO NOT use yawn. this is one tick up from yawn.
my_measurement_sens = 0.007042253389954567

my_normal_min_inc = get_min_inc_from_sens(my_sens)
my_measurement_min_inc = get_min_inc_from_sens(my_measurement_sens)

# make sure we're not on yawn.
assert get_min_inc_from_sens(my_measurement_sens) > 0.01

# Angle from first f3+c
# /execute in minecraft:overworld run tp @s 25.50 64.00 0.50 -983.06 -3.20
measured_angle = -41.55
real_precise_angle = 96.944580

precise_start = get_precise_start_angle(measured_angle, my_normal_min_inc)

print(precise_start)

# Angle from second f3+c
eye_measured_angle = -41.79
pixel_count = -1

precise_offset = get_precise_start_angle(eye_measured_angle-precise_start, my_measurement_min_inc)

true_sh_angle = precise_start-precise_offset+(pixel_count*get_degrees_per_pixel(16384))

# print(my_measurement_min_inc)
print(f'True angle: {true_sh_angle}')

diff = abs(real_precise_angle-get_displayed_angle(precise_start))
diff_per_min_inc = diff/(measured_angle/my_normal_min_inc)
# print(f'{diff:+.10f}')
# print(f'{diff_per_min_inc:+.20f}')