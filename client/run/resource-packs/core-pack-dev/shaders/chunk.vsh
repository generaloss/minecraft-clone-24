#version 330

in vec3 v_position;
in vec2 v_texcoord;
in vec4 v_color;
in vec3 v_normal;
in vec3 v_light;

out vec2 f_texcoord;
out vec4 f_color;
out vec3 f_normal;
out vec3 f_light;

uniform mat4 u_combined;
uniform mat4 u_tick;

void main() {
    gl_Position = u_combined * vec4(v_position, 1.0);
    f_texcoord = v_texcoord;
    f_color = v_color;
    f_normal = v_normal;
    f_light = v_light;
}