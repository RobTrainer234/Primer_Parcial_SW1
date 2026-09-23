---
name: Obsidian CAD
colors:
  surface: '#0b1326'
  surface-dim: '#0b1326'
  surface-bright: '#31394e'
  surface-container-lowest: '#060d20'
  surface-container-low: '#131b2e'
  surface-container: '#171f33'
  surface-container-high: '#222a3e'
  surface-container-highest: '#2d3449'
  on-surface: '#dbe2fd'
  on-surface-variant: '#c7c4d7'
  inverse-surface: '#dbe2fd'
  inverse-on-surface: '#283044'
  outline: '#908fa0'
  outline-variant: '#464554'
  surface-tint: '#c0c1ff'
  primary: '#c0c1ff'
  on-primary: '#1000a9'
  primary-container: '#8083ff'
  on-primary-container: '#0d0096'
  inverse-primary: '#494bd6'
  secondary: '#4cd7f6'
  on-secondary: '#003640'
  secondary-container: '#03b5d4'
  on-secondary-container: '#00424e'
  tertiary: '#c3c0ff'
  on-tertiary: '#2c2a5e'
  tertiary-container: '#7371aa'
  on-tertiary-container: '#020026'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e1e0ff'
  primary-fixed-dim: '#c0c1ff'
  on-primary-fixed: '#07006c'
  on-primary-fixed-variant: '#2f2ebe'
  secondary-fixed: '#acedff'
  secondary-fixed-dim: '#4cd7f6'
  on-secondary-fixed: '#001f26'
  on-secondary-fixed-variant: '#004e5c'
  tertiary-fixed: '#e3dfff'
  tertiary-fixed-dim: '#c3c0ff'
  on-tertiary-fixed: '#161349'
  on-tertiary-fixed-variant: '#434176'
  background: '#0b1326'
  on-background: '#dbe2fd'
  surface-variant: '#2d3449'
typography:
  headline-lg:
    fontFamily: Space Grotesk
    fontSize: 30px
    fontWeight: '600'
    lineHeight: 38px
  headline-lg-mobile:
    fontFamily: Space Grotesk
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  headline-md:
    fontFamily: Space Grotesk
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 26px
  headline-sm:
    fontFamily: Space Grotesk
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 22px
  body-lg:
    fontFamily: Geist
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-md:
    fontFamily: Geist
    fontSize: 13px
    fontWeight: '400'
    lineHeight: 18px
  body-sm:
    fontFamily: Geist
    fontSize: 11px
    fontWeight: '400'
    lineHeight: 16px
  code-md:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  code-sm:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '400'
    lineHeight: 14px
  label-md:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
  label-sm:
    fontFamily: JetBrains Mono
    fontSize: 10px
    fontWeight: '600'
    lineHeight: 12px
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  gutter: 0.75rem
  gutter-mobile: 0.5rem
  margin: 1rem
  margin-mobile: 0.5rem
  space-xs: 0.25rem
  space-sm: 0.375rem
  space-md: 0.75rem
  space-lg: 1.25rem
  space-xl: 1.75rem
---

## Brand & Style

This design system defines a high-density, professional engineering workbench built for enterprise architects, systems designers, and software engineers executing Model-Driven Architecture (MDA).

The emotional tone balances absolute precision, zero-latency responsiveness, and mission-critical reliability. It rejects toy-like consumer SaaS conventions in favor of a focused, technical "studio workbench" vibe: calibrated contrast ratios, sharp informational hierarchy, exact geometry, and surgical CAD canvas clarity.

Key style pillars:
- **Precision CAD Engineering**: Sharp boundaries, visible grid intersections, and micro-metric structural accents evoke architectural CAD suites.
- **Instrument-Grade Information Density**: Compact, legible typography with balanced monospaced code notation allows hundreds of classes, relationships, and state machine elements to live concurrently on-screen without visual clutter.
- **Atmospheric Dark Canvas**: Deep obsidian/navy negative space reduces ocular fatigue during sustained 10-hour modeling sessions, illuminated by electrical neon accents indicating execution paths, validation errors, and multi-user telemetry.

## Colors

The palette simulates a luminous technical blueprint on deep space obsidian. Every color serves a functional taxonomic purpose across the canvas and tool palettes.

- **Primary (`#6366f1` Indigo Electric)**: Used for primary canvas operations, selected nodes, active connectors, focused wireframe bounds, and commit triggers.
- **Secondary (`#4cd7f6` Cyan Neon)**: Signal trace color representing compilation status, active interface conduits, verified UML stereotype decorators, and real-time cursor indicators for remote collaborators.
- **Tertiary (`#c3c0ff` Polar Violet)**: Semantic annotation accent applied to UML metadata, data types, multiplicity markers (`[0..*]`), visibility operators (`+`, `-`, `#`), and documentation flyouts.
- **Neutral (`#0b1326` Deep Obsidian)**: The baseline coordinate root. Panels step up progressively to `#111a33` (Surfaces), `#182242` (Elevated Layers/Modals), and `#1f2c56` (Hover/Active states).
- **Text & Stroke Scale**: Text hierarchy relies on crisp clarity with primary engineering readout text at `#dae2fd` and secondary schema metadata at `#94a3b8`. Divider and stroke lines use razor-thin structural borders (`#223158`).

## Typography

The type system is constructed for dual-purpose technical consumption: high-speed UI navigation and dense notation reading.

- **Headline Display (`Space Grotesk`)**: Technical, mechanical, and geometric. Used for project namespaces, diagram titles, architectural package roots, and top-tier metrics.
- **Interface & Content (`Geist`)**: Neutral, ultra-legible, pixel-snapping grotesque used for inspector trees, property grids, contextual tooltips, and system dialogs.
- **Stereotypes, Types, & Metamodel (`JetBrains Mono`)**: Strict tabular alignment for attributes (`+ id: UUID [1]`), method signatures, constraints (`{readOnly}`), and multi-user telemetry coordinate tags.

## Layout & Spacing

The layout is a dense, dockable viewport canvas framed by technical panels. The core canvas remains fluid, unbounded, and scale-independent, governed by a physical 8px coordinate snapping matrix with visual sub-grids at 1px intervals.

- **Desktop Workbench Matrix**:
  - Left dock (Package Explorer / Diagram Tree): Fixed 280px or 320px width.
  - Center stage: Infinite virtualized SVG/WebGL canvas space with continuous viewport pan and zoom controls.
  - Right dock (UML 2.5 Metamodel Inspector / OCL Constraint Engine): Fixed 340px width.
  - Bottom panel (Validation Warnings / Output Console): Collapsible, defaulting to 180px height.
- **Grid & Alignment Rhythm**: Micro-increments (`space-xs` = 4px, `space-sm` = 6px, `space-md` = 12px) preserve critical screen real estate. Form rows within inspectors use rigid 28px standard heights.
- **Breakpoints & Mobile Fallback**: Below 1024px, side docks collapse into slide-over technical overlays, leaving the primary view optimized for model review, inspection, and redlining.

## Elevation & Depth

Visual hierarchy does not use soft, fuzzy drop shadows. Instead, it relies on **tonal stepping combined with crisp structural CAD outlines and localized luminescence**:

- **Ground Level 0 (Infinite Canvas Grid)**: Raw deep obsidian `#0b1326`, marked with isometric or Cartesian dot grid coordinates at `#18233f`.
- **Level 1 (Docked Workspaces & Side Panels)**: `#111a33` surface bound by a 1px solid border of `#223158`.
- **Level 2 (Canvas Nodes & Floating Toolbars)**: `#152040` with an outer hairline stroke of `#2c3f70`. Unselected entities maintain zero shadow.
- **Level 3 (Selected CAD Entities & Dragged Elements)**: Highlighted with an exact 1.5px `#6366f1` boundary accompanied by a localized electrical back-glow (`0 0 12px rgba(99, 102, 241, 0.35)`).
- **Level 4 (Modals, Context Menus, and Active Collaborator Cursors)**: `#182242` backing, 1px `#4cd7f6` stroke accent on active edge, and dark technical containment shadow (`0 8px 24px -4px rgba(0, 4, 16, 0.8)`).

## Shapes

The geometry reflects surgical industrial instruments:
- `roundedness: 1` sets a tight 4px default curve across panels, inputs, chips, and modals, eliminating wasted radius territory while softening harsh micro-angles.
- Canvas nodes (UML Classes, Interfaces, Use Cases) strictly adhere to 2px or 4px radii to ensure precise connection anchors for orthogonal and splined bezier relationship lines.
- Relationship end-caps (Generalization triangles, Composition diamonds, Dependency open arrows) enforce crisp non-rounded Euclidean points.
- Collaborative live presence pills and status dots leverage fully circular geometry to distinguish transient human users from static software constructs.

## Components

### UML Canvas Nodes (Classes & Interfaces)
- **Header**: `#152040` background with a lower 1px border (`#223158`). Stereotypes rendered in `JetBrains Mono` (`label-sm`) using `#c3c0ff` (e.g., `«interface»`). Class title rendered in bold `Geist` (`body-md`) `#dae2fd`.
- **Compartments**: Alternating zero-padding sections for attributes and operations. Hovering a row displays micro-controls for accessibility modifiers (`public (+)`, `private (-)`, `protected (#)`).
- **Selection State**: Outer 1.5px `#6366f1` perimeter with 8 physical 6x6px square anchor handles at cardinal and ordinal coordinates.

### Buttons & Quick-Action Triggers
- **Primary**: Solid `#6366f1` background, `#ffffff` text, 28px height, 4px border radius. Hover introduces `#4cd7f6` border transition with 150ms ease.
- **Canvas Icon Actions**: Ghost styling on `#111a33`, 1px border `#223158`, icon color `#94a3b8`. Active or toggled state turns background to `#1f2c56` with `#4cd7f6` foreground.
- **Destructive**: Low-luminance crimson background (`#3b111e`), perimeter line `#ef4444`, text `#fca5a5`.

### Input Fields & Property Inspectors
- **Grid Inputs**: 24px-28px height. Dark slate fill `#0d152a`, border 1px `#223158`, text `#dae2fd` in `JetBrains Mono`. Focus yields a sharp 1px ring in `#4cd7f6` with zero blur.
- **Inline Multi-State Selectors**: Segmented pill bars encased in a 1px frame with active state illuminated via `#1e2952`.

### Chips, Tags & Stereotypes
- **Stereotype Badges**: Height 18px, padding 2px 6px. Transparent background with a 1px `#3b4874` border, text `#c3c0ff`, strictly uppercase `label-sm`.
- **OCL Validation Chips**: Error status tags use a `#2a121d` fill, `#f87171` text, and a 1px `#dc2626` stroke. Verified valid markers use `#0d282e` fill, `#34d399` text, and `#059669` stroke.

### Collaborative Real-Time Elements
- **Peer Cursor**: Hairline 1.5px vector pointer tinted in the collaborator's assigned dynamic hue (e.g., `#4cd7f6`, `#a855f7`, `#f59e0b`).
- **Presence Badge**: Pill tag adjacent to cursor displaying user monograms or short names in `label-sm`, backdrop `#0b1326cc` with 8px backdrop blur and 1px border matching the user's signature color.
- **Selection Halo**: Active peer selections outline objects in a semi-transparent dashed highlight (`2px dashed [user-color]`) with a 0.15 opacity fill overlay.

### Checkboxes, Radio Buttons, & Tree Lists
- **Checkboxes**: 14x14px sharp squares (`roundedness: 1`), border 1px `#334373`. Checked state provides `#6366f1` fill with an optical white CAD checkmark.
- **Tree Lists (Package Hierarchy)**: Indented 12px per nesting tier, connected by 1px dotted guide-lines (`#1e2c52`). Active node highlighted via `#1a2649` full-width row fill and left 2px `#6366f1` accent bar.