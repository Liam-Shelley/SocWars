package com.soc.lib;

import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static com.soc.lib.SocWarsLib.mapFromRange;

public final class YSplitStructureTemplate {
	private final List<StructureTemplate> templates;

	public YSplitStructureTemplate(StructureTemplate template) {
		this.templates = split(template);
	}

	private static List<StructureTemplate> split(StructureTemplate template) { //TODO: Test this; I have 0 clue if it works
		final int height = template.getSize().getY();

		final List<StructureTemplate> layers = mapFromRange(0, height, i -> {
			final StructureTemplate layer = new StructureTemplate();
			layer.author = template.author;
			layer.size = new Vec3i(template.size.getX(), 1, template.size.getZ());
			return layer;
		});

		for (StructureTemplate.StructureEntityInfo entity : template.entities) {
			final int layer = Math.clamp(entity.blockPos.getY(), 0, height - 1);
			layers.get(layer).entities.add(entity);
		}


		final List<List<StructureTemplate.StructureBlockInfo>> infoListList = mapFromRange(0, height, i -> new ArrayList<>());

		for (int i = 0; i < template.blockInfoLists.size(); i++) {
			for (StructureTemplate.StructureBlockInfo info : template.blockInfoLists.get(i).getAll()) {
				final int layer = info.pos().getY();
				infoListList.get(layer).add(info);
			}
		}

		for (int i = 0; i < infoListList.size(); i++) {
			layers.get(i).blockInfoLists.add(new StructureTemplate.PalettedBlockInfoList(infoListList.get(i)));
		}

		return layers;
	}

	public List<StructureTemplate> getLayers() {
		return this.templates;
	}

	public StructureTemplate getLayer(int layer) {
		return this.templates.get(layer);
	}

	public int layers() {
		return this.templates.size();
	}

	public void placeAllLayers(ServerWorldAccess serverWorld, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, Random random, int flags) {
		this.templates.forEach(template -> template.place(serverWorld, pos, pivot, placementData, random, flags));
	}

	public Iterator<StructureTemplate> iterator() {
		return new Itr();
	}

	private class Itr implements Iterator<StructureTemplate> {
		private int cursor = 0;

		@Override
		public boolean hasNext() {
			return this.cursor != YSplitStructureTemplate.this.templates.size();
		}

		@Override
		public StructureTemplate next() {
			if (this.cursor >= YSplitStructureTemplate.this.templates.size()) throw new NoSuchElementException();

			return YSplitStructureTemplate.this.templates.get(this.cursor++);
		}
	}
}
