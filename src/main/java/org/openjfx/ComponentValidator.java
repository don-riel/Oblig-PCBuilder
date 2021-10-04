package org.openjfx;


import org.openjfx.exceptions.InvalidCoreSpeedException;
import org.openjfx.exceptions.InvalidHDCapacityException;
import org.openjfx.exceptions.InvalidRamCapacityException;
import org.openjfx.exceptions.InvalidScreenSizeException;

public class ComponentValidator {
    public static boolean checkCoreClock(double core) throws InvalidCoreSpeedException {
        if(core < 1.1 || core > 4.7) {
            throw new InvalidCoreSpeedException("Use core speed between 1.1 - 4-7 Ghz!");
        }
        return true;
    }

    public static boolean checkHdCapacity(int cap) throws InvalidHDCapacityException {
        if(cap < 8 || cap > 16000) {
            throw new InvalidHDCapacityException("Use capacity value between 8 - 16000 Gb!");
        }
        return true;
    }

    public static boolean checkMemoryCapacity(int cap) throws InvalidRamCapacityException {
        if(cap < 1 || cap > 32) {
            throw new InvalidRamCapacityException("Use capacity value between 1 - 32 Gb!");
        }
        return true;
    }

    public static boolean checkScreenSize(double size) throws InvalidScreenSizeException {
        if(size < 16 || size > 65) {
            throw new InvalidScreenSizeException("Use screen size between 16 - 65 inches!");
        }
        return true;
    }
}

