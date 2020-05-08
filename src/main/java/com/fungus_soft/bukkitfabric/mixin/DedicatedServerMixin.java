package com.fungus_soft.bukkitfabric.mixin;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginLoadOrder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.fungus_soft.bukkitfabric.bukkitimpl.FakeLogger;
import com.fungus_soft.bukkitfabric.bukkitimpl.FakeServer;
import com.fungus_soft.bukkitfabric.interfaces.IMixinCommandOutput;

import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.text.Text;

@Mixin(MinecraftDedicatedServer.class)
public class DedicatedServerMixin implements CommandOutput, IMixinCommandOutput {

    @Inject(at = @At(value = "HEAD"), method = "setupServer()Z") // TODO keep ordinal updated
    private void initVar(CallbackInfoReturnable<Boolean> callbackInfo) {
        FakeServer.server = (MinecraftDedicatedServer) (Object) this;
    }

    @Inject(at = @At(value = "JUMP", ordinal = 8), method = "setupServer()Z") // TODO keep ordinal updated
    private void init(CallbackInfoReturnable<Boolean> callbackInfo) {
        FakeLogger.getLogger().info("  ____          _     _     _  _    ");
        FakeLogger.getLogger().info(" |  _ \\        | |   | |   (_)| |   ");
        FakeLogger.getLogger().info(" | |_) | _   _ | | __| | __ _ | |_  ");
        FakeLogger.getLogger().info(" |  _ < | | | || |/ /| |/ /| || __| ");
        FakeLogger.getLogger().info(" | |_) || |_| ||   < |   < | || |_  ");
        FakeLogger.getLogger().info(" |____/  \\__,_||_|\\_\\|_|\\_\\|_| \\__| ");
        FakeLogger.getLogger().info("");
        Bukkit.setServer(new FakeServer((MinecraftDedicatedServer) (Object) this));

        Bukkit.getLogger().info("Loading Bukkit plugins...");
        File pluginsDir = new File("plugins");
        pluginsDir.mkdir();

        FakeServer s = ((FakeServer)Bukkit.getServer());
        if (FakeServer.server == null) FakeServer.server = (MinecraftDedicatedServer) (Object) this;

        s.loadPlugins();
        s.enablePlugins(PluginLoadOrder.STARTUP);
        
        Bukkit.getLogger().info("");
    }

    @Inject(at = @At(value = "RETURN"), method = "setupServer()Z")
    private void finish(CallbackInfoReturnable<Boolean> callbackInfo) {
        FakeServer s = ((FakeServer)Bukkit.getServer());

        s.enablePlugins(PluginLoadOrder.POSTWORLD);
    }

    @Override
    public CommandSender getBukkitSender(ServerCommandSource serverCommandSource) {
        return Bukkit.getConsoleSender();
    }

    @Override
    public boolean sendCommandFeedback() {
        return false;
    }

    @Override
    public void sendMessage(Text message) {
        Bukkit.getConsoleSender().sendMessage(message.toString());
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }

    @Override
    public boolean shouldTrackOutput() {
        return false;
    }

}