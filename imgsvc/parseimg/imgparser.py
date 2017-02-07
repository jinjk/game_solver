import numpy as np
import cv2
import math


class Pos(object):
    """position object"""
    def __init__(self, x, y, w, h):
        self.x = x
        self.y = y
        self.w = w
        self.h = h

CONST_LINE_NUM = 10
CONST_STAR_VAR = 100

def main():
    """main function"""
    fd = open('test.png', 'rb')
    data = fd.read()
    fd.close()
    parse_from_data(data, CONST_LINE_NUM)

def parse_from_data(data, line_num = CONST_LINE_NUM):
    ''' parse img from file data'''
    nparr = np.fromstring(data, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR) # cv2.IMREAD_COLOR in OpenCV 3.1
    return parse_img(img, line_num)

def parse_img(img, line_num):
    '''parse img data to get star matrix'''
    height, width, channels = img.shape
    imgray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    ret,thresh = cv2.threshold(imgray,30,255,0)
    img2, contours, hierarchy = cv2.findContours(
        thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    my_buffer = []
    rects = []
    row_idx = 0
    last_y = -1
    last_w = -1
    for cnt in reversed(contours):
        x, y, w, h = cv2.boundingRect(cnt)
        if last_y == -1:
            last_y = y
            last_w = w
        elif math.fabs(last_y - y) < 3 and math.fabs(last_w - w) < 3:
            row_idx += 1
        if x < 5:
            row_idx = 1

        p = Pos(x, y, w, h)
        my_buffer.append(p)
        last_y = y
        last_w = w
        if row_idx == line_num and width - (x + w) < 5:
            rects.extend(my_buffer[-line_num:])
            del my_buffer[:]

    mat = to_symbol_list(rects, img, line_num)
    return mat
'''
    cv2.namedWindow('image', cv2.WINDOW_NORMAL)
    cv2.imshow('image',img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()
'''

def to_symbol_list(rects, img, line_num):
    color_symbol = {}
    symbol_value = 0
    symbol_list = []
    
    for p in rects:
        img_snipet = img[p.y:p.y+p.h, p.x:p.x+p.w]
        avg_color = avg_value(img_snipet)
        found = False
        for key in color_symbol.keys():
            var = color_var(key, avg_color)
            if var < CONST_STAR_VAR:
                symbol_list.append(color_symbol[key])
                found = True
                break
        
        if not found:
            symbol_value += 1
            color_symbol[avg_color] = symbol_value
            symbol_list.append(symbol_value)

        cv2.rectangle(img, (p.x, p.y), (p.x + p.w, p.y + p.h), (0, 200, 0), 2)
    
    return np.reshape(symbol_list, (-1, line_num)).tolist()

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
