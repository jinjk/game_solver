import numpy as np
import cv2
#from matplotlib import pyplot as plt
import math
import functools as ft


class Pos(object):
    """position object"""
    def __init__(self, x, y, w, h):
        self.x = x
        self.y = y
        self.w = w
        self.h = h

CONST_COL_NUM = 10
CONST_STAR_VAR = 100

def main():
    """main function"""
    fd = open('test.png', 'rb')
    data = fd.read()
    fd.close()
    parse_from_data(data, CONST_COL_NUM)

def parse_from_data(data, col_num = CONST_COL_NUM):
    ''' parse img from file data'''
    nparr = np.fromstring(data, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR) # cv2.IMREAD_COLOR in OpenCV 3.1
    return parse_img(img, col_num)

def filter_rects(width, margin, col_num, rects):
    sw = width / col_num # star rect width

    rects = list(filter(lambda p: 0.6 < p.w / sw < 1 and \
               0.6 < p.h / sw < 1 and 0.85 < p.w / p.h < 1.2, rects))
    edge_rects = filter(lambda p: p.x < margin, rects)
    edge_rects = sorted(edge_rects, key=lambda p: p.y, reverse=True)

    left_bottom_box = edge_rects[0]

    y_begin = left_bottom_box.y - sw * 9.2
    y_end = left_bottom_box.y + sw * 0.2

    rects = filter(lambda p: y_begin < p.y < y_end, rects)

    def compare(p1, p2):
        if p1.x > p2.x and math.fabs(p1.y - p2.y) < margin:
            return 1
        else:
            return -1

    rects = sorted(rects, key=ft.cmp_to_key(compare))
    return rects, y_begin


def parse_img(img, col_num):
    '''parse img data to get star matrix'''
    height, width, channels = img.shape

    margin = width / 10 * 0.2
    least_len = width / 10 * 0.3
    max_len = width / 10

    imgray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    thresh = cv2.adaptiveThreshold(imgray,255,cv2.ADAPTIVE_THRESH_MEAN_C, \
            cv2.THRESH_BINARY,31,2)
    img2, contours, hierarchy = cv2.findContours(
        thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

#    black = np.zeros([height,width,3],dtype=np.uint8)
    my_buffer = []
    rects = []
    star_rects = []
    row_idx = 0
    last_y = -1
    last_w = -1

    for cnt in contours:
        x, y, w, h = cv2.boundingRect(cnt)
        p = Pos(x, y, w, h)
        rects.append(p)

    rects, y_begin = filter_rects(width, margin, col_num, rects)


    mat, icons = to_symbol_list(rects, img, col_num, width, y_begin)
    return mat, icons
'''
    print(mat)
    return mat
    img = cv2.imread('messi5.jpg',0)
    plt.imshow(black, cmap = 'gray', interpolation = 'bicubic')
    plt.show()
    return mat
'''

def to_symbol_list(rects, img, col_num, width, y_begin):
    color_symbol = {}
    symbol_value = 0
    icons = {}
    sw = width / col_num

    mat = np.zeros(shape=(col_num, col_num), dtype=np.int8)

    for p in rects:
        x = int(p.x // sw)
        y = int((p.y - y_begin) // sw)

        img_snipet = img[p.y:p.y+p.h, p.x:p.x+p.w]
        avg_color = avg_value(img_snipet)
        found = False
        for key in color_symbol.keys():
            var = color_var(key, avg_color)
            if var < CONST_STAR_VAR:
                mat[y][x] = color_symbol[key]
                found = True
                break
        
        if not found:
            symbol_value += 1
            color_symbol[avg_color] = symbol_value
            icons[symbol_value] = p
            mat[y][x] = symbol_value

    return mat.tolist(), icons

def color_var(a, b):
    a = np.array(a)
    b = np.array(b)
    var = np.mean(np.abs(a - b)**2)
    return var

def avg_value(img):
    'get the average color vaule of a image'
    average_color_per_row = np.average(img, axis=0)
    average_color = np.average(average_color_per_row, axis=0)
    int_color = [int(round(f)) for f in average_color]
    return tuple(int_color)


if __name__ == '__main__':
    main()
