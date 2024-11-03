/*
 * This file is part of Mixin, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ru.artem.alaverdyan.injections;

/**
 * CallbackInfo instances are passed to callbacks in order to provide
 * information and handling opportunities to the callback to interact with the
 * callback itself. For example by allowing the callback to be "cancelled" and
 * return from a method prematurely. 
 */
public class CallbackInfo implements Cancellable {

    private final String name;

    /**
     * True if this callback is cancellable
     */
    private final boolean cancellable;


    private boolean cancelled;

    public CallbackInfo(String name, boolean cancellable) {
        this.name = name;
        this.cancellable = cancellable;
    }

    public String getId() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("CallbackInfo[TYPE=%s,NAME=%s,CANCELLABLE=%s]", this.getClass().getSimpleName(), this.name, this.cancellable);
    }

    @Override
    public final boolean isCancellable() {
        return this.cancellable;
    }

    @Override
    public final boolean isCancelled() {
        return this.cancelled;
    }

    /* (non-Javadoc)
     * @see org.spongepowered.asm.mixin.injection.callback.Cancellable#cancel()
     */
    @Override
    public void cancel() throws CancellationException {
        if (!this.cancellable) {
            throw new CancellationException(String.format("The call %s is not cancellable.", this.name));
        }

        this.cancelled = true;
    }
    
    // Methods below this point used by the CallbackInjector

    static String getCallInfoClassName() {
        return CallbackInfo.class.getName();
    }

    static String getIsCancelledMethodName() {
        return "isCancelled";
    }

    static String getIsCancelledMethodSig() {
        return "()Z";
    }
}
