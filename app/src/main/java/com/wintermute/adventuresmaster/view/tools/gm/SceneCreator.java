package com.wintermute.adventuresmaster.view.tools.gm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.wintermute.adventuresmaster.R;
import com.wintermute.adventuresmaster.view.custom.SceneAudioEntry;
import com.wintermute.adventuresmaster.viewmodel.CreateSceneViewModel;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * In this activity a scene is composed and the responsible view model ({@link CreateSceneViewModel}) is notified to
 * persist these changes once the user want to save it.
 *
 * @author wintermute
 */
public class SceneCreator extends AppCompatActivity
    implements SceneAudioEntry.OnSelectAudioClick, SceneAudioEntry.OnPlayAudioClick, SceneAudioEntry.OnChangedVolume,
    SceneAudioEntry.OnChangeLoopingStatus, SceneAudioEntry.OnChangeSchedulerStatus
{
    private String audioEntryType;
    private SceneAudioEntry effect, music, ambience;
    private CreateSceneViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scene_activity);

        initComponents();
    }

    private void initComponents()
    {
        model = new ViewModelProvider(this).get(CreateSceneViewModel.class);

        Button preview = findViewById(R.id.scene_activity_scene_preview);
        preview.setOnClickListener(v -> scenePreview());

        Button save = findViewById(R.id.scene_activity_save_scene);
        save.setOnClickListener(v ->
        {
            TextView sceneNameView = findViewById(R.id.scene_activity_scene_name);
            String sceneName = sceneNameView.getText().toString();
            if ("".equals(sceneName))
            {
                Toast.makeText(this, "Name must not be empty", Toast.LENGTH_SHORT).show();
                sceneNameView.setHintTextColor(Color.RED);
                return;
            }
            storeScene(sceneName);
            model.stopGameAudioPlayer();
            finish();
        });

        effect = findViewById(R.id.scene_activity_effect);
        effect.disablePlayAfterEffectOption();
        music = findViewById(R.id.scene_activity_music);
        ambience = findViewById(R.id.scene_activity_ambience);

        Arrays.asList(effect, music, ambience).forEach(a ->
        {
            a.setOnSelectAudioClick(this);
            a.setOnPlayAudioClick(this);
            a.setOnChangedVolume(this);
            a.setOnChangedLoopingStatus(this);
            a.setOnChangeSchedulerStatus(this);
        });
    }

    private void scenePreview()
    {
        model.playSceneForPreview(this);
    }

    private void storeScene(String sceneName)
    {
        model.storeSceneAndAudio(this, sceneName, getIntent().getLongExtra("inBoard", 0L));
    }

    private String sanitizeFileName(String path)
    {
        String result = new File(path).getName();
        return result.length() < 27 ? result : result.substring(0, 25) + "...";
    }

    @Override
    public void onSelectAudioClickListener(String tag)
    {
        audioEntryType = tag;
        startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("audio/*"), 1);
    }

    @Override
    public void onPlayClickListener(String tag)
    {
        model.playTrack(this, tag);
    }

    @Override
    public void onChangedVolume(int progress, String tag)
    {
        model.updateTrackVolume(tag, progress);
    }

    @Override
    public void onChangeLoopingStatus(boolean loop, String tag)
    {
        model.updateLoopingStatus(loop, tag);
    }

    @Override
    public void OnChangeSchedulerStatus(boolean playAfterIntro, String tag)
    {
        model.updateTrackScheduling(playAfterIntro, tag);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        model.stopGameAudioPlayer();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            String selectedFilePath = Objects.requireNonNull(data.getData()).getPath();
            if ("effect".equals(audioEntryType))
            {
                model.prepareAudioFileWithOpts(effect, data.getDataString());
                effect.setSceneAudioFileTitle(sanitizeFileName(selectedFilePath));
            } else if ("music".equals(audioEntryType))
            {
                model.prepareAudioFileWithOpts(music, data.getDataString());
                music.setSceneAudioFileTitle(sanitizeFileName(selectedFilePath));
            }
            if ("ambience".equals(audioEntryType))
            {
                model.prepareAudioFileWithOpts(ambience, data.getDataString());
                ambience.setSceneAudioFileTitle(sanitizeFileName(selectedFilePath));
            }
        }
    }
}